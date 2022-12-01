package com.example.routes

import com.example.data.model.Users
import com.example.data.model.requests.AuthRequest
import com.example.data.model.responses.AuthMessageResponse
import com.example.data.model.responses.AuthResponse
import com.example.data.source.UserDataSource
import com.example.service.JwtTokenService
import com.example.service.security.hashing.HashingService
import com.example.service.security.hashing.SaltedHash
import com.example.service.security.token.TokenClaim
import com.example.service.security.token.TokenConfig
import com.example.util.Const
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    login(userDataSource, hashingService, tokenService, tokenConfig)
    register(userDataSource, hashingService, tokenService, tokenConfig)
}

private fun Route.login(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    post(Const.LOGIN) {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST))
            return@post
        }
        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Forbidden, AuthMessageResponse(Const.INVALID_CREDENTIALS))
            return@post
        }

        val isValidPassword = validatePassword(hashingService, request, user)
        if (!isValidPassword) {
            call.respond(HttpStatusCode.Forbidden, AuthMessageResponse(Const.INVALID_CREDENTIALS))
            return@post
        }

        val token = generateToken(tokenService, tokenConfig, user)
        val success = Const.LOGIN_SUCCESSFUL
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(success, token)
        )
    }
}

private fun validatePassword(
    hashingService: HashingService,
    request: AuthRequest,
    user: Users
) = hashingService.verify(
    value = request.password,
    saltedHash = SaltedHash(
        hash = user.password,
        salt = user.salt
    )
)

private fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    post(Const.REGISTER) {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST))
            return@post
        }

        val areFieldsBlank = request.username.isBlank() && request.password.isBlank() && request.email.isBlank()
        val isUsernameShort = request.username.length < 3
        val isPasswordShort = request.password.length < 8
        val isEmailInvalid = !request.email.contains("@") || !request.email.contains(".") || request.email.length < 5

        val username = userDataSource.getUserByUsername(request.username)

        /**
         * Validations
         */
        if (username != null) {
            call.respond(HttpStatusCode.Conflict, AuthMessageResponse(Const.USER_ALREADY_EXISTS))
            return@post
        }

        if (areFieldsBlank) {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST_EMPTY_FIELDS))
            return@post
        }

        if (isUsernameShort) {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST_SHORT_USERNAME))
            return@post
        }

        if (isPasswordShort) {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST_SHORT_PASSWORD))
            return@post
        }

        if (isEmailInvalid) {
            call.respond(HttpStatusCode.BadRequest, AuthMessageResponse(Const.INVALID_REQUEST_EMAIL))
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = getUser(request, saltedHash)
        val wasAcknowledged = userDataSource.createUser(user)
        val token = generateToken(tokenService, tokenConfig, user)
        if (wasAcknowledged) {
            call.respond(
                HttpStatusCode.Created,
                AuthResponse(Const.REGISTER_SUCCESSFUL, token)
            )
        } else {
            call.respond(HttpStatusCode.InternalServerError, AuthMessageResponse(Const.REGISTER_FAILED))
        }
    }
}

private fun getUser(
    request: AuthRequest,
    saltedHash: SaltedHash
) = Users(
    username = request.username,
    email = request.email,
    password = saltedHash.hash,
    salt = saltedHash.salt
)

private fun generateToken(
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig,
    user: Users
) = tokenService.generate(
    config = tokenConfig,
    TokenClaim(
        name = "userId",
        value = user.id.toString()
    )
)
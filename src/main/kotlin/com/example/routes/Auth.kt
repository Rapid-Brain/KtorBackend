package com.example.routes

import com.example.data.UserDataSource
import com.example.data.model.User
import com.example.data.model.requests.AuthRequest
import com.example.data.model.responses.AuthResponse
import com.example.service.JwtTokenService
import com.example.service.security.hashing.HashingService
import com.example.service.security.hashing.SaltedHash
import com.example.service.security.token.TokenClaim
import com.example.service.security.token.TokenConfig
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
    register(userDataSource, hashingService)
}

private fun Route.login(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    post("login") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Invalid request!")
            return@post
        }
        val user = userDataSource.getUserByUsername(request.username)
        if (user == null) {
            call.respond(HttpStatusCode.Forbidden, "Incorrect username or password!")
            return@post
        }

        val isValidPassword = hashingService.verify(
            value = request.password,
            saltedHash = SaltedHash(
                hash = user.password,
                salt = user.salt
            )
        )

        if (!isValidPassword) {
            call.respond(HttpStatusCode.Forbidden, "Incorrect username or password!")
            return@post
        }

        val token = tokenService.generate(
            config = tokenConfig,
            TokenClaim(
                name = "userId",
                value = user.id.toString()
            )
        )

        val success = "Successfully logged in!"
        call.respond(
            status = HttpStatusCode.OK,
            message = AuthResponse(success, token)
        )
    }
}

private fun Route.register(
    userDataSource: UserDataSource,
    hashingService: HashingService
) {
    post("register") {
        val request = kotlin.runCatching { call.receiveNullable<AuthRequest>() }.getOrNull() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest, "Invalid request!")
            return@post
        }

        val areFieldsBlank = request.username.isBlank() && request.password.isBlank()
        val isUsernameShort = request.username.length < 3
        val isPasswordShort = request.password.length < 8
        val username = userDataSource.getUserByUsername(request.username)

        if (username != null) {
            call.respond(HttpStatusCode.Conflict, "Username already exists!")
            return@post
        }

        if (areFieldsBlank) {
            call.respond(HttpStatusCode.BadRequest, "Username and password cannot be blank!")
            return@post
        }

        if (isUsernameShort) {
            call.respond(HttpStatusCode.BadRequest, "Username must be at least 3 characters long!")
            return@post
        }

        if (isPasswordShort) {
            call.respond(HttpStatusCode.BadRequest, "Password must be at least 8 characters long!")
            return@post
        }

        val saltedHash = hashingService.generateSaltedHash(request.password)
        val user = User(
            username = request.username,
            password = saltedHash.hash,
            salt = saltedHash.salt
        )
        val wasAcknowledged = userDataSource.createUser(user)
        if (wasAcknowledged) call.respond(HttpStatusCode.Created, "User created successfully!")
        else call.respond(HttpStatusCode.Conflict, "User creation failed! Please check your request.")
    }
}
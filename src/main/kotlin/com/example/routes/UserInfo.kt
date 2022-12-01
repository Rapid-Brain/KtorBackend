package com.example.routes

import com.example.data.model.responses.UserInfoResponse
import com.example.util.Const
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.getUserInfo() {
    authenticate {
        get(Const.USER_INFO) {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.getClaim("userId", String::class)
            val username = principal?.getClaim("username", String::class)
            val emailAddress = principal?.getClaim("email", String::class)

            userId ?: return@get
            username ?: return@get
            emailAddress ?: return@get

            if (userId.isEmpty() || username.isEmpty() || emailAddress.isEmpty()) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid token!")
                return@get
            }

            call.respond(HttpStatusCode.OK, UserInfoResponse(userId, username, emailAddress))
        }
    }
}
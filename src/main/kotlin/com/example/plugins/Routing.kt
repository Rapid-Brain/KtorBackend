package com.example.plugins

import com.example.data.source.UserDataSource
import com.example.routes.authRoutes
import com.example.routes.homeRoute
import com.example.service.JwtTokenService
import com.example.service.security.hashing.HashingService
import com.example.service.security.token.TokenConfig
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    userDataSource: UserDataSource,
    hashingService: HashingService,
    tokenService: JwtTokenService,
    tokenConfig: TokenConfig
) {
    routing {
        authRoutes(userDataSource, hashingService, tokenService, tokenConfig)
        homeRoute()
    }
}



package com.example

import com.example.data.source.MongoUserDataSource
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.service.JwtTokenService
import com.example.service.security.hashing.SHA256HashingService
import com.example.service.security.token.TokenConfig
import com.example.util.Const
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>): Unit = EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    val mongoPassword = System.getenv("mongoPassword")
    val dbName = System.getenv("dbName")

    val db =
        KMongo.createClient("Your MongoDB connection string")
            .coroutine
            .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()

    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()
    val tokenConfig = tokenConfig(issuer, audience)
    val hashingService = SHA256HashingService()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(userDataSource, hashingService, tokenService, tokenConfig)
}

private fun tokenConfig(
    issuer: String,
    audience: String
) = TokenConfig(
    issuer = issuer,
    audience = audience,
    expiresIn = Const.expiresIn,
    secret = System.getenv("JWT_SECRET")
)

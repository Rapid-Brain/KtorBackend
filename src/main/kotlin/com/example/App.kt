package com.example

import com.example.data.MongoUserDataSource
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.service.JwtTokenService
import com.example.service.security.hashing.SHA256HashingService
import com.example.service.security.token.TokenConfig
import com.example.util.Constants
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
        KMongo.createClient(connectionString = "your connection string here..")
            .coroutine
            .getDatabase(dbName)

    val userDataSource = MongoUserDataSource(db)
    val tokenService = JwtTokenService()

    val issuer = environment.config.property("jwt.issuer").getString()
    val audience = environment.config.property("jwt.audience").getString()

    val tokenConfig = TokenConfig(
        issuer = issuer,
        audience = audience,
        expiresIn = Constants.expiresIn,
        secret = System.getenv("JWT_SECRET")
    )
    val hashingService = SHA256HashingService()

    configureMonitoring()
    configureSerialization()
    configureSecurity(tokenConfig)
    configureRouting(
        userDataSource = userDataSource,
        hashingService = hashingService,
        tokenService = tokenService,
        tokenConfig = tokenConfig
    )
}

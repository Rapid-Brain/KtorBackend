package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.service.security.token.TokenClaim
import com.example.service.security.token.TokenConfig
import com.example.service.security.token.TokenService
import java.util.*

class JwtTokenService : TokenService {

    override fun generate(config: TokenConfig, vararg claim: TokenClaim): String {
        val expirationDate = Date(System.currentTimeMillis() + 60000)
        var token = JWT.create()
            .withAudience(config.audience)
            .withIssuer(config.issuer)
            .withExpiresAt(expirationDate)
        claim.forEach { claim ->
            token = token.withClaim(claim.name, claim.value)
        }
        return token.sign(Algorithm.HMAC256(config.secret))
    }
}
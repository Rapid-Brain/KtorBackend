package com.example.service.security.hashing

data class SaltedHash(
    val hash: String,
    val salt: String
)
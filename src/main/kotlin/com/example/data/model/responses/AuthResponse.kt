package com.example.data.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val message: String,
    val token: String
)
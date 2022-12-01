package com.example.data.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class AuthMessageResponse(
    val message: String
)
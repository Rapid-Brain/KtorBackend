package com.example.data.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoResponse(
    val id: String,
    val username: String,
    val email: String
)
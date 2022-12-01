package com.example.data.source

import com.example.data.model.Users

interface UserDataSource {

    suspend fun getUserByUsername(username: String): Users?

    suspend fun createUser(user: Users): Boolean
}
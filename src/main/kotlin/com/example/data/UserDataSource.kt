package com.example.data

import com.example.data.model.User

interface UserDataSource {

    suspend fun getUserByUsername(username: String): User?

    suspend fun createUser(user: User): Boolean
}
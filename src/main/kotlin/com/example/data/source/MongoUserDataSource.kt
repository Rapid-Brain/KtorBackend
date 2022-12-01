package com.example.data.source

import com.example.data.model.Users
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class MongoUserDataSource(db: CoroutineDatabase) : UserDataSource {

    private val users = db.getCollection<Users>()

    override suspend fun getUserByUsername(username: String): Users? {
        return users.findOne(Users::username eq username)
    }

    override suspend fun createUser(user: Users) = users.insertOne(user).wasAcknowledged()
}
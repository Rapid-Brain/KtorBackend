package com.example.data.model

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Users(
    @BsonId
    val id: ObjectId = ObjectId(),
    val username: String,
    val email: String,
    val password: String,
    val salt: String
)
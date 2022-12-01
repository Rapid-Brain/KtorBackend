package com.example.util

object Const {

    /**
     * Error messages
     */
    const val INVALID_REQUEST = "Invalid request!"
    const val LOGIN_SUCCESSFUL = "Successfully logged in!"
    const val INVALID_CREDENTIALS = "Incorrect username or password!"
    const val USER_ALREADY_EXISTS = "User already exists!"
    const val INVALID_REQUEST_EMPTY_FIELDS = "Username and password cannot be blank!"
    const val INVALID_REQUEST_SHORT_USERNAME = "Username must be at least 3 characters long!"
    const val INVALID_REQUEST_SHORT_PASSWORD = "Password must be at least 8 characters long!"
    const val INVALID_REQUEST_EMAIL = "Invalid email address!"
    const val REGISTER_SUCCESSFUL = "The user has been registered successfully!"
    const val REGISTER_FAILED = "Registration failed! Please check your request."

    /**
     * Endpoints paths
     */
    const val LOGIN = "api/login"
    const val REGISTER = "api/register"
    const val USER_INFO = "api/user_info"

    /**
     * Miscellaneous
     */
    const val expiresIn = 365L * 1000L * 60L * 24L
}
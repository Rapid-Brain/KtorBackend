package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.homeRoute() {
    val indexHtml = File("./static/index.html")
    get("/") {
        call.respondFile(indexHtml)
    }
}
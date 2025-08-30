package com.perrigogames.life4.api

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun baseHttpClient(
    json: Json,
    log: co.touchlab.kermit.Logger
) = HttpClient {
    install(ContentNegotiation) {
        json(json)
        register(ContentType.Text.Any, KotlinxSerializationConverter(json))
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                log.v { message }
            }
        }

        level = LogLevel.INFO
    }
    install(HttpTimeout) {
        val timeout = 30000L
        connectTimeoutMillis = timeout
        requestTimeoutMillis = timeout
        socketTimeoutMillis = timeout
    }
}
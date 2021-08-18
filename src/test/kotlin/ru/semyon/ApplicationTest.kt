package ru.semyon

import io.ktor.application.*
import io.ktor.features.*
import ru.dsl.ObjectMother
import io.ktor.http.*
import io.ktor.serialization.*
import kotlin.test.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import ru.semyon.plugins.configureRouting

class ApplicationTest {
    private val create = ObjectMother()

    @Test
    fun testRoot() {
        withTestApplication({
            install(ContentNegotiation){
                json()
            }
            configureRouting()
        }){
            with(handleRequest(HttpMethod.Post, "/generateOpenHours"){
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(Json.encodeToString(create.weekRequest().please()))
            }) {
                assertEquals("""
                    Monday: 12:00 AM - 12:20 AM
                    Tuesday: 12:00 AM - 12:20 AM
                    Wednesday: 12:00 AM - 12:20 AM
                    Thursday: 12:00 AM - 12:20 AM
                    Friday: 12:00 AM - 12:20 AM
                    Saturday: 12:00 AM - 12:20 AM
                    Sunday: 12:00 AM - 12:20 AM
                """.trimIndent(), response.content)
            }
        }
    }
}
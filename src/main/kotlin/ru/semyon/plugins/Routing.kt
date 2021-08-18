package ru.semyon.plugins

import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import ru.semyon.model.GenerationResult
import ru.semyon.model.WeekRequest
import ru.semyon.service.OpenHoursService

fun Application.configureRouting() {
    routing {
        post("/generateOpenHours") {
            runCatching {
                val request = call.receive<WeekRequest>()
                when (val result = OpenHoursService().generateOpenHours(request)) {
                    is GenerationResult.Success -> call.respondText(result.result)
                    is GenerationResult.Failure -> call.respondText(
                        result.error,
                        ContentType.Text.Plain,
                        HttpStatusCode.BadRequest
                    )
                }
            }.onFailure { ex ->
                call.respondText(
                    ex.message ?: "Something went wrong ${ex.stackTrace}",
                    ContentType.Text.Plain,
                    HttpStatusCode.BadRequest
                )
            }
        }
    }
}

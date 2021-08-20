package ru.semyon.service

import ru.semyon.model.*
import ru.semyon.service.Validator.Companion.validateCloseHours
import ru.semyon.service.Validator.Companion.validate
import ru.semyon.service.Validator.Companion.validateOpenHours
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class OpenHoursService {

    fun generateOpenHours(request: WeekRequest): GenerationResult {
        return runCatching {
            val result = StringBuilder()
            request.prepare().forEach {
                result.appendLine(generate(it.first, it.second))
            }
            GenerationResult.Success(result.trimEnd().toString())
        }.getOrElse { ex ->
            GenerationResult.Failure(ex.message ?: ex.stackTrace.toString())
        }
    }

    private fun WeekRequest.prepare(): List<Pair<String, List<WorkHours>>> {
        val days = mutableListOf(
            Days.MONDAY.name to this.monday.toMutableList(),
            Days.TUESDAY.name to this.tuesday.toMutableList(),
            Days.WEDNESDAY.name to this.wednesday.toMutableList(),
            Days.THURSDAY.name to this.thursday.toMutableList(),
            Days.FRIDAY.name to this.friday.toMutableList(),
            Days.SATURDAY.name to this.saturday.toMutableList(),
            Days.SUNDAY.name to this.sunday.toMutableList()
        ).onEach(::validate)

        days.forEachIndexed { dayNumber, day ->
            val todayWorkHours = day.second
            if (todayWorkHours.size % 2 != 0 || todayWorkHours.firstOrNull()?.type == CLOSE) {
                when (todayWorkHours.first().type.takeIf { it == CLOSE } ?: OPEN) {
                    OPEN -> {
                        validateOpenHours(days, dayNumber)
                        if (dayNumber < days.size - 1) {
                            days[dayNumber + 1].second.removeFirstOrNull()?.let {
                                todayWorkHours.add(it)
                            }
                        } else {
                            days.first().second.removeFirstOrNull()?.let {
                                todayWorkHours.add(it)
                            }
                        }
                    }
                    CLOSE -> {
                        validateCloseHours(days, dayNumber)
                        if (dayNumber != 0) {
                            todayWorkHours.removeFirstOrNull()?.let {
                                days[dayNumber - 1].second.add(it)
                            }
                        } else {
                            todayWorkHours.removeFirstOrNull()?.let {
                                days.last().second.add(it)
                            }
                        }
                    }
                }
            }
        }
        return days
    }

    private fun generate(dayName: String, workHours: List<WorkHours>): String {
        val formattedDayName = dayName.lowercase(Locale.getDefault()).replaceFirstChar {
            it.titlecase(Locale.getDefault())
        }

        return when {
            workHours.isEmpty() -> "$formattedDayName: Closed"
            else -> {
                val workHourResult = StringBuilder()
                val formattedWorkHours = mutableListOf<String>()
                workHours.forEach { workHour ->
                    when (workHour.type) {
                        OPEN -> workHourResult.append("${workHour.value.getTime().uppercase()} - ")
                        CLOSE -> {
                            workHourResult.append(workHour.value.getTime().uppercase())
                            formattedWorkHours.add(workHourResult.toString())
                            workHourResult.clear()
                        }
                    }
                }
                "$formattedDayName: ${formattedWorkHours.joinToString(separator = ", ")}"
            }
        }
    }

    private fun Long.getTime() =
        LocalTime.ofSecondOfDay(this).format(DateTimeFormatter.ofPattern("h:mm a"))


    companion object {
        const val OPEN = "open"
        const val CLOSE = "close"
    }
}
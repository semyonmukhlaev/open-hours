package ru.semyon.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class WeekRequest(
    @Required
    var monday: List<WorkHours> = listOf(),
    @Required
    var tuesday: List<WorkHours> = listOf(),
    @Required
    var wednesday: List<WorkHours> = listOf(),
    @Required
    var thursday: List<WorkHours> = listOf(),
    @Required
    var friday: List<WorkHours> = listOf(),
    @Required
    var saturday: List<WorkHours> = listOf(),
    @Required
    var sunday: List<WorkHours> = listOf()
)
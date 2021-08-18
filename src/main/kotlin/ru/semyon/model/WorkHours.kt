package ru.semyon.model

import kotlinx.serialization.Required
import kotlinx.serialization.Serializable

@Serializable
data class WorkHours(
    @Required
    val type: String,
    @Required
    val value: Long
) {
    init {
        require(type in listOf("open", "close")) {
            "Check 'type' field in request JSON. Only 'open' and 'close' allowed, but it contains '$type'."
        }

        require(value in 0..86399) {
            "Check 'value' field in request JSON. Value should be in range from 0 to 86399, but it contains $value."
        }
    }
}
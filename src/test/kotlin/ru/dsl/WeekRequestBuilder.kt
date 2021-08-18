package ru.dsl

import ru.semyon.model.WeekRequest
import ru.semyon.model.WorkHours

class WeekRequestBuilder {
    private var weekRequest = WeekRequest(
        monday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        tuesday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        wednesday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        thursday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        friday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        saturday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        ),
        sunday = listOf(
            WorkHours("open", 0),
            WorkHours("close", 1234)
        )
    )

    fun withEachDayCloseTomorrow(): WeekRequestBuilder {
        weekRequest = WeekRequest(
            monday = listOf(
                WorkHours("close", 67000),
            ),
            tuesday = listOf(
                WorkHours("open", 63000)
            ),
            wednesday = listOf(
                WorkHours("close", 39600),
            ),
            thursday = listOf(
                WorkHours("open", 63000)
            ),
            friday = listOf(
                WorkHours("close", 39600),
            ),
            saturday = listOf(
                WorkHours("open", 63000)
            ),
            sunday = listOf(
                WorkHours("close", 39600),
                WorkHours("open", 63000)
            )
        )
        return this
    }

    fun withChanges(buildAction: (WeekRequest) -> Unit) = apply {
        buildAction(weekRequest)
    }

    fun please() = weekRequest
}
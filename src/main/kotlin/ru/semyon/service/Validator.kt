package ru.semyon.service

import ru.semyon.model.WorkHours

class Validator {
    companion object {
        fun validate(day: Pair<String, MutableList<WorkHours>>) {
            val workHours = day.second
            workHours.forEachIndexed { index, todayWorkHour ->
                if (index < workHours.size - 1)
                    require(todayWorkHour.type != workHours[index + 1].type) {
                        "${day.first} has work hours duplicate: ${workHours[index + 1].type} - ${workHours[index + 1].value} after ${todayWorkHour.type} - ${todayWorkHour.value}"
                    }

                if (index < workHours.size - 1 && workHours.size > 1 && todayWorkHour.type == "open")
                    require(workHours[index + 1].value >= todayWorkHour.value) {
                        "${day.first}: Close hour should be greater than open hour"
                    }
            }
        }

        fun validateCloseHours(
            days: MutableList<Pair<String, MutableList<WorkHours>>>,
            dayNumber: Int
        ) {
            if (dayNumber != FIRST_WEEK_DAY)
                require(days[dayNumber - 1].second.isNotEmpty() && days[dayNumber - 1].second.last().type != OpenHoursService.CLOSE) {
                    "${days[dayNumber].first} has to start with 'open' work hour or ${days[dayNumber - 1].first} has to end with 'open' work hour."
                }
            else
                require(days.last().second.isNotEmpty() && days.last().second.last().type != OpenHoursService.CLOSE) {
                    "${days[dayNumber].first} has to start with 'open' work hour or ${days[dayNumber - 1].first} has to end with 'open' work hour."
                }
        }

        fun validateOpenHours(
            days: MutableList<Pair<String, MutableList<WorkHours>>>,
            dayNumber: Int
        ) {
            if (dayNumber == LAST_WEEK_DAY) {
                val firstDayWorkHours = days.first().second
                require(firstDayWorkHours.isNotEmpty() && firstDayWorkHours.first().type != OpenHoursService.OPEN) {
                    "${days.first().first} has to start with 'close' work hours."
                }
            }
            else {
                val nextDayWorkHours = days[dayNumber + 1].second
                require(nextDayWorkHours.isNotEmpty() && nextDayWorkHours.first().type != OpenHoursService.OPEN) {
                    "${days[dayNumber + 1].first} has to start with 'close' work hours."
                }
            }
        }

        private const val FIRST_WEEK_DAY = 0
        private const val LAST_WEEK_DAY = 6
    }
}
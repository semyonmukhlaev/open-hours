package ru.semyon

import ru.dsl.ObjectMother
import ru.semyon.model.GenerationResult
import ru.semyon.model.WorkHours
import ru.semyon.service.OpenHoursService
import spock.lang.Specification
import spock.lang.Unroll

class OpenHoursServiceShould extends Specification {
    def service = new OpenHoursService()
    def create = new ObjectMother()

    def "generate open hours when each day has open and close hours"() {
        given:
        def request = create.weekRequest().please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Success.class
        (result as GenerationResult.Success).result ==
                """\
                Monday: 12:00 AM - 12:20 AM
                Tuesday: 12:00 AM - 12:20 AM
                Wednesday: 12:00 AM - 12:20 AM
                Thursday: 12:00 AM - 12:20 AM
                Friday: 12:00 AM - 12:20 AM
                Saturday: 12:00 AM - 12:20 AM
                Sunday: 12:00 AM - 12:20 AM""".stripIndent()
    }

    def "generate open hours when each day has close hours next day"() {
        given:
        def request = create.weekRequest().withEachDayCloseTomorrow().please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Success.class
        (result as GenerationResult.Success).result ==
                """\
                Monday: Closed
                Tuesday: 5:30 PM - 11:00 AM
                Wednesday: Closed
                Thursday: 5:30 PM - 11:00 AM
                Friday: Closed
                Saturday: 5:30 PM - 11:00 AM
                Sunday: 5:30 PM - 6:36 PM""".stripIndent()
    }

    def "generate open hours when every day has no open hours"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = []
            it.tuesday = []
            it.wednesday = []
            it.thursday = []
            it.friday = []
            it.saturday = []
            it.sunday = []
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Success.class
        (result as GenerationResult.Success).result ==
                """\
                Monday: Closed
                Tuesday: Closed
                Wednesday: Closed
                Thursday: Closed
                Friday: Closed
                Saturday: Closed
                Sunday: Closed""".stripIndent()
    }

    def "throw exception if there is incorrect open hour type in request"() {
        when:
        create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("strangeType", 12345)
            ]
        }.please()

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Check 'type' field in request JSON. Only 'open' and 'close' allowed, but it contains 'strangeType'."
    }

    @Unroll
    def "throw exception when open hour value in request = #workHourValue"() {
        when:
        create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", workHourValue)
            ]
        }.please()

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Check 'value' field in request JSON. Value should be in range from 0 to 86399, but it contains $workHourValue."

        where:
        workHourValue << [-1, 999999]
    }

    def "generate open hours when Monday and Sunday have 3 WorkHours"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("close", 23200),
                    new WorkHours("open", 32400),
                    new WorkHours("close", 39600)
            ]
            it.sunday = [
                    new WorkHours("open", 43200),
                    new WorkHours("close", 75600),
                    new WorkHours("open", 75900)
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Success.class
        (result as GenerationResult.Success).result ==
                """\
                Monday: 9:00 AM - 11:00 AM
                Tuesday: 12:00 AM - 12:20 AM
                Wednesday: 12:00 AM - 12:20 AM
                Thursday: 12:00 AM - 12:20 AM
                Friday: 12:00 AM - 12:20 AM
                Saturday: 12:00 AM - 12:20 AM
                Sunday: 12:00 PM - 9:00 PM, 9:05 PM - 6:26 AM""".stripIndent()
    }

    def "fail generation with error when day ends with open, next day starts with open"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", 32400),
                    new WorkHours("close", 39600),
                    new WorkHours("open", 40000),

            ]
            it.tuesday = [
                    new WorkHours("open", 43200),
                    new WorkHours("close", 75600),
                    new WorkHours("open", 75900)
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error == "TUESDAY has to start with 'close' work hours."
    }

    def "fail generation with error when open value greater than close value"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", 2),
                    new WorkHours("close", 1),
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error == "MONDAY: Close hour should be greater than open hour"
    }

    def "fail generation with error when day has no open work hour"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", 1),
                    new WorkHours("close", 2),
            ]
            it.tuesday = [
                    new WorkHours("close", 1)
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error ==
                "TUESDAY has to start with 'open' work hour or MONDAY has to end with 'open' work hour."
    }

    def "fail generation with error when day misses close work hour"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", 1),
                    new WorkHours("close", 2),
                    new WorkHours("open", 3),
            ]
            it.tuesday = [
                    new WorkHours("open", 4)
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error ==
                "TUESDAY has to start with 'close' work hours."
    }

    def "fail generation with error when day has open after open work hour"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("open", 1),
                    new WorkHours("open", 2),
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error ==
                "MONDAY has work hours duplicate: open - 2 after open - 1"
    }

    def "fail generation with error when day has close after close work hour"() {
        given:
        def request = create.weekRequest().withChanges {
            it.monday = [
                    new WorkHours("close", 1),
                    new WorkHours("close", 2),
            ]
        }.please()

        when:
        def result = service.generateOpenHours(request)

        then:
        result.class == GenerationResult.Failure.class
        (result as GenerationResult.Failure).error ==
                "MONDAY has work hours duplicate: close - 2 after close - 1"
    }
}
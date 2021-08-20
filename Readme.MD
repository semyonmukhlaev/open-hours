## Running

### Your can run application using docker:

Run next commands in terminal in project's root directory:

`gradlew clean installDist`

`docker build -t open-hours .`

`docker run -p 8080:8080 open-hours`

### Or run in IDE:

Build application `gradlew build` then run `main` method in Application.kt

Then send post request 0.0.0.0:8080/generateOpenHours with proper json.

Also, there are unit tests in 'test' folder.

## Question
Is there a limit for closing hours if a restaurant is open one day and 
closed the next day?

For example:
Opening hour for Monday is 9AM and closing hour is 11AM the next day.
In this case it will look like
Monday: 9AM-11AM.

I would limit the closing hours for these cases, say, by 6am.  

## Suggestion
I suggest using 24-hour format, because it seems more suitable and convenient.
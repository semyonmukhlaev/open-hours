## Question
What if open hour for monday is 9AM and close hour is in next day for 11AM? 

It will look like 9AM-11AM.

Seems that 24-hour format is more suitable.

#Running
`gradlew clean installDist`

`docker build -t open-hours .`

`docker run -p 8080:8080 open-hours`
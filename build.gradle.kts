val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.21"
    kotlin("plugin.serialization") version "1.5.21"

    groovy
}

group = "ru.semyon"
version = "0.0.1"
application {
    mainClass.set("ru.semyon.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("io.ktor:ktor-client-cio:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")

    implementation("org.codehaus.groovy:groovy-all:3.0.8")
    testImplementation("org.spockframework:spock-core:2.0-groovy-3.0")
}

tasks.compileTestGroovy {
    val kotlinCompile = tasks.compileTestKotlin.get()
    dependsOn(kotlinCompile)
    this.classpath += files(kotlinCompile.destinationDir)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

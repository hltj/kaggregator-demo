import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.10"
    application
}

group = "me.hltj"
version = "1.0-SNAPSHOT"

application.mainClassName = "io.ktor.server.cio.EngineMain"

repositories {
    jcenter()
    maven(url = "https://kotlin.bintray.com/kotlinx")
    mavenCentral()
}

val ktorVersion = "1.0.0"
fun ktor(module: String) = "io.ktor:ktor-$module:$ktorVersion"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(ktor("server-cio"))
    implementation(ktor("client-cio"))
    implementation(ktor("client-jackson"))
    implementation(ktor("jackson"))
    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
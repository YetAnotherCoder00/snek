plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.2"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")


    implementation("uk.co.electronstudio.jaylib:jaylib:4.2.+")
}

tasks.test {
    useJUnitPlatform()
}
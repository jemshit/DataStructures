plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.jemshit"
version = "0.1"

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
    maxHeapSize = "1g"
}

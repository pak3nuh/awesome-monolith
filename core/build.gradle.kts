plugins {
    id("io.github.pak3nuh.monolith.kotlin-library-conventions")
}


dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    // todo update to latest coroutine along with java/kotlin version
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
}
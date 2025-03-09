import org.gradle.kotlin.dsl.testImplementation

plugins {
    id("java")
}

group = "uk.chaoticgoose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation("org.mockito:mockito-core:3.+")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    jvmArgs = listOf(
        "--enable-preview",
    )
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
    options.release.set(23)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}
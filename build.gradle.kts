import net.ltgt.gradle.errorprone.errorprone
import org.gradle.kotlin.dsl.implementation
import org.gradle.kotlin.dsl.testImplementation

plugins {
    id("java")
    id("net.ltgt.errorprone") version "4.2.0"
}

group = "uk.chaoticgoose"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("com.tngtech.archunit:archunit:1.4.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.2")
    testImplementation("org.mockito:mockito-core:3.+")

    implementation("org.jspecify:jspecify:1.0.0")
    errorprone("com.uber.nullaway:nullaway:0.12.7")
    errorprone("com.google.errorprone:error_prone_core:2.38.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.release.set(24)
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        disableAllChecks = true // Other error prone checks are disabled
        option("NullAway:OnlyNullMarked", "true") // Enable nullness checks only in null-marked code
        error("NullAway") // bump checks from warnings (default) to errors
    }
    if (name.lowercase().contains("test")) {
        options.errorprone {
            disable("NullAway")
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}
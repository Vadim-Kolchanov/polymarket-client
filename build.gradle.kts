plugins {
    kotlin("jvm") version "2.2.0"
    id("co.uzzu.dotenv.gradle") version "4.0.0"
    id("com.vanniktech.maven.publish") version "0.35.0"
}

group = "dev.kolchanov"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_21

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.10.2")

    // Jackson Kotlin Support
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.2")

    // Web3
    implementation("org.web3j:core:4.14.0")

    // Websocket
    implementation("org.java-websocket:Java-WebSocket:1.5.4")

    // HTTP Client
    implementation("com.squareup.okhttp3:okhttp:5.3.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.3.0")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("org.slf4j:slf4j-simple:2.0.16")

    // Testing
    testImplementation(kotlin("test"))
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment = env.allVariables()
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()
}

mavenPublishing {
    coordinates(group.toString(), name.toString(), version.toString())

    pom {
        name.set("Polymarket Client")
        description.set("A Kotlin client for interacting with the Polymarket prediction market platform.")
        inceptionYear.set("2025")
        url.set("https://github.com/Vadim-Kolchanov/polymarket-client")

        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("Vadim-Kolchanov")
                name.set("Vadim Kolchanov")
                url.set("https://github.com/Vadim-Kolchanov/")
                email.set("kolchanov.offer@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/Vadim-Kolchanov/polymarket-client.git")
            developerConnection.set("scm:git:ssh://github.com/Vadim-Kolchanov/polymarket-client.git")
            url.set("https://github.com/Vadim-Kolchanov/polymarket-client/")
        }
    }
}

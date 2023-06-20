import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

// Configurar los repositorios para resolver las dependencias del proyecto
repositories {
    mavenCentral()
    maven { uri("https://jitpack.io").also { url = it } }
}

// Configurar los plugins necesarios para el proyecto
plugins {
    kotlin("jvm") version "1.9.0-Beta"
    id("java")
    id("maven-publish")
}

// Definir la versión de Java a utilizar
fun JavaPluginExtension.compileOptions() {
    JavaVersion.VERSION_20.also { sourceCompatibility = it }
    JavaVersion.VERSION_20.also { targetCompatibility = it }
}

// Configurar las dependencias del proyecto
dependencies {
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.apache.commons:commons-compress:1.23.0")
    implementation("org.jboss.slf4j:slf4j-jboss-logging:1.2.1.Final")
}

// Configurar el proyecto
group = "corp.agnifenix"
version = "1.0-SNAPSHOT"

// Configurar la publicación de artefactos en GitHub Packages
publishing {
    repositories {
        maven {
            name = "DataBaseManipulation"
            url = uri("https://maven.pkg.github.com/AgniFenix/DataBaseManipulation")
            credentials {
                username = project.findProperty("fex.user") as String? ?: System.getenv("USERNAME")
                password = if (project.findProperty("useTokenClassic") == "true" || System.getenv("TOKENCLASSIC") == "true") {
                    project.findProperty("fex.key") as String? ?: System.getenv("TOKENCLASSIC")
                } else {
                    project.findProperty("fex.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }
    publications {
        register("fex", MavenPublication::class) {
            from(components["java"])
        }
    }
}

// Configurar las opciones de compilación de Kotlin
tasks.withType<KotlinCompile> {
    "19".also { kotlinOptions.jvmTarget = it }
    "1.9".also { kotlinOptions.apiVersion = it }
}

configurations.all { resolutionStrategy.cacheChangingModulesFor(0, "seconds") }
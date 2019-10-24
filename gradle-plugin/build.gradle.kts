import net.minecrell.gradle.licenser.LicenseExtension

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.1"
    id("com.techshroom.incise-blue")
    kotlin("jvm")
}

inciseBlue {
    ide()
    license()
    util {
        javaVersion = JavaVersion.VERSION_1_8
    }
}

configure<LicenseExtension> {
    include("**/*.kt")
}

dependencies {
    "implementation"(project(":elivi-common"))
    "implementation"(kotlin("stdlib-jdk8", embeddedKotlinVersion))
}

pluginBundle {
    website = "https://github.com/octylFractal/elivi-code-compressor"
    vcsUrl = "https://github.com/octylFractal/elivi-code-compressor.git"
    tags = listOf("obfuscator", "elivi", "java")
}
gradlePlugin {
    plugins {
        create("elivi") {
            id = "net.octyl.elivi"
            displayName = "Elivi Code Compressor"
            description = "Compresses code by removing & renaming class file elements."
            implementationClass = "net.octyl.elivi.EliviPlugin"
        }
    }
}


rootProject.tasks.named("afterReleaseBuild").configure {
    dependsOn(tasks.named("publishPlugins"))
}

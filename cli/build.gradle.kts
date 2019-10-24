import net.minecrell.gradle.licenser.LicenseExtension

plugins {
    application
    id("com.techshroom.incise-blue")
    kotlin("jvm")
}

inciseBlue {
    ide()
    license()
    util {
        javaVersion = JavaVersion.VERSION_1_8
    }
    maven {
        projectDescription = "Code Compressor for Java (CLI Tool)"
        coords("octylFractal", "elivi-code-compressor")
        artifactName = "${rootProject.name}-${project.name}"
    }
}

configure<LicenseExtension> {
    include("**/*.kt")
}

dependencies {
    "implementation"(project(":elivi-common"))
    "implementation"(kotlin("stdlib-jdk8"))
    "implementation"("com.github.ajalt:clikt:2.2.0")
}

application.mainClassName = "net.octyl.elivi.MainKt"

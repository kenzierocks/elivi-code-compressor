import net.minecrell.gradle.licenser.LicenseExtension

plugins {
    application
    id("com.techshroom.incise-blue") version "0.4.0"
    id("net.researchgate.release") version "2.8.0"
    kotlin("jvm") version "1.3.50"
}

inciseBlue {
    ide()
    license()
    util {
        javaVersion = JavaVersion.VERSION_1_8
    }
    maven {
        projectDescription = "Code Compressor for Java"
        coords("kenzierocks", "elivi-code-compressor")
    }
}

configure<LicenseExtension> {
    include("**/*.kt")
}

plugins.withId("maven-publish") {
    tasks.named("afterReleaseBuild").configure {
        dependsOn(tasks.named("publish"))
    }
}

dependencies {
    "implementation"(kotlin("stdlib-jdk8"))
    "implementation"("com.github.ajalt:clikt:2.2.0")
    val asmVersion = "7.2"
    "implementation"("org.ow2.asm:asm:$asmVersion")
    "implementation"("org.ow2.asm:asm-commons:$asmVersion")
}

application.mainClassName = "net.octyl.elivi.MainKt"

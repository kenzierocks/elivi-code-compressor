import net.minecrell.gradle.licenser.LicenseExtension

plugins {
    application
    id("com.techshroom.incise-blue") version "0.4.0"
    kotlin("jvm") version "1.3.50"
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
    "implementation"(kotlin("stdlib-jdk8"))
    "implementation"("com.github.ajalt:clikt:2.2.0")
    val asmVersion = "7.2"
    "implementation"("org.ow2.asm:asm:$asmVersion")
    "implementation"("org.ow2.asm:asm-commons:$asmVersion")
}

application.mainClassName = "net.octyl.elivi.MainKt"

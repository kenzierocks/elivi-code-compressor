import com.techshroom.inciseblue.maven.License
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
    nexus {
        projectDescription.set("Code Compressor for Java (CLI Tool)")
        coords("octylFractal", "elivi-code-compressor")
        license(License.MIT)
        addDeveloper("octylFractal", "Octavia Togami", "octavia.togami@gmail.com")
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

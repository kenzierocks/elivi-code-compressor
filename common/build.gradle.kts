import net.minecrell.gradle.licenser.LicenseExtension

plugins {
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
        projectDescription = "Code Compressor for Java (Common Code)"
        coords("octylFractal", "elivi-code-compressor")
        artifactName = "${rootProject.name}-${project.name}"
    }
}

configure<LicenseExtension> {
    include("**/*.kt")
}

dependencies {
    "implementation"(kotlin("stdlib-jdk8"))
    "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.2")
    val asmVersion = "7.2"
    "implementation"("org.ow2.asm:asm:$asmVersion")
    "implementation"("org.ow2.asm:asm-commons:$asmVersion")
}

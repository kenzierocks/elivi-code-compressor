import com.techshroom.inciseblue.maven.License
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
    nexus {
        projectDescription.set("Code Compressor for Java (Common Code)")
        coords("octylFractal", "elivi-code-compressor")
        license(License.MIT)
        addDeveloper("octylFractal", "Octavia Togami", "octavia.togami@gmail.com")
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

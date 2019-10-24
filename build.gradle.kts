plugins {
    base
    id("com.techshroom.incise-blue") version "0.4.0"
    id("net.researchgate.release") version "2.8.0"
    kotlin("jvm") version "1.3.50" apply false
}

inciseBlue {
    util {
        javaVersion = JavaVersion.VERSION_1_8
    }
}

subprojects {
    plugins.withId("maven-publish") {
        rootProject.tasks.named("afterReleaseBuild").configure {
            dependsOn(tasks.named("publish"))
        }
    }

    plugins.withId("base") {
        rootProject.tasks.named("build").configure {
            dependsOn(tasks.named("build"))
        }
    }
}

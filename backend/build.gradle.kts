/*
 * Copyright (c) 2021 Mustafa Ozhan. All rights reserved.
 */

plugins {
    with(Plugins) {
        application
        kotlin(multiplatform)
    }
}

with(ProjectSettings) {
    application {
        mainClass.set("$packageName.backend.BackendAppKt")
    }
    group = projectId
    version = getVersionName(project)
}

kotlin {
    jvm {
        withJava()
    }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jvmMain by getting {
            dependencies {
                with(Dependencies.JVM) {
                    implementation(ktorCore)
                    implementation(ktorNetty)
                    implementation(ktorSerialization)
                    implementation(logBack)
                }

                with(Dependencies.Common) {
                    implementation(koinCore)
                }

                with(Modules) {
                    implementation(project(common))
                    implementation(project(logmob))
                }
            }
        }
    }
}

tasks.register<Jar>("fatJar") {
    archiveBaseName.set("${project.name}-fat")
    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = ProjectSettings.getVersionName(project)
        attributes["Main-Class"] = "${ProjectSettings.packageName}.backend.BackendAppKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

@Suppress("UnstableApiUsage")
tasks.named<ProcessResources>("jvmProcessResources") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

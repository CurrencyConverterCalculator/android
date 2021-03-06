/*
 * Copyright (c) 2021 Mustafa Ozhan. All rights reserved.
 */
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    with(Plugins) {
        kotlin(multiplatform)
        id(kotlinXSerialization)
        id(androidLibrary)
        id(sqldelight)
        id(buildKonfig)
    }
}

kotlin {

    android()

    // todo Revert to just ios() when gradle plugin can properly resolve it
    if (System.getenv("SDK_NAME")?.startsWith("iphoneos") == true) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    jvm()

    // todo enable when implementation start
    // js { browser { binaries.executable()testTask { enabled = false } } }

    @Suppress("UNUSED_VARIABLE")
    sourceSets {

        with(Dependencies.Common) {
            val commonMain by getting {
                dependencies {
                    implementation(project(Modules.logmob))

                    implementation(multiplatformSettings)
                    implementation(dateTime)
                    implementation(koinCore)
                    implementation(ktorLogging)
                    implementation(ktorSerialization)
                    implementation(sqldelightRuntime)
                    implementation(sqldelightCoroutineExtensions)
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(kotlin(test))
                    implementation(kotlin(testAnnotations))
                }
            }
        }

        with(Dependencies.Android) {
            val androidMain by getting {
                dependencies {
                    implementation(sqlliteDriver)
                    implementation(ktor)
                }
            }
            val androidTest by getting {
                dependencies {
                    implementation(kotlin(Dependencies.JVM.testJUnit))
                }
            }
        }

        with(Dependencies.IOS) {
            val iosMain by getting {
                dependencies {
                    implementation(ktor)
                    implementation(sqlliteDriver)
                }
            }
            val iosTest by getting
        }

        with(Dependencies.JVM) {
            val jvmMain by getting {
                dependencies {
                    implementation(ktor)
                    implementation(sqlliteDriver)
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(testJUnit))
                }
            }
        }

        // todo enable when implementation start
        // with(Dependencies.JS) { val jsMain by getting { dependencies { implementation(ktor) } }
        // val jsTest by getting { dependencies { implementation(kotlin(test))  } } }
    }
}

android {
    with(ProjectSettings) {
        compileSdk = compileSdkVersion

        defaultConfig {
            minSdk = minSdkVersion
            targetSdk = targetSdkVersion
        }

        sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

sqldelight {
    with(Database) {
        database(dbName) {
            packageName = dbPackageName
            sourceFolders = listOf(dbSourceFolders)
        }
    }
}

configure<BuildKonfigExtension> {
    packageName = "${ProjectSettings.packageName}.common"

    defaultConfigs {
        buildConfigField(
            STRING,
            Keys.baseUrlBackend,
            getSecret(Keys.baseUrlBackend, Fakes.privateUrl)
        )
        buildConfigField(STRING, Keys.baseUrlApi, getSecret(Keys.baseUrlApi, Fakes.privateUrl))
        buildConfigField(STRING, Keys.baseUrlDev, getSecret(Keys.baseUrlDev, Fakes.privateUrl))
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }
}

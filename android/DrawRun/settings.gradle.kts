import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }  // JetBrains Compose 레포지토리 추가
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        // Mapbox Maven repository
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"

                // local.properties에서 MAPBOX_DOWNLOADS_TOKEN 읽기
                val localPropertiesFile = rootDir.resolve("local.properties")
                if (localPropertiesFile.exists()) {
                    val properties = Properties().apply {
                        load(localPropertiesFile.inputStream())
                    }
                    password = properties.getProperty("MAPBOX_DOWNLOADS_TOKEN")
                        ?: error("MAPBOX_DOWNLOADS_TOKEN is missing in local.properties")
                } else {
                    error("local.properties file not found. Please create it and add MAPBOX_DOWNLOADS_TOKEN.")
                }
            }
        }
    }
}


rootProject.name = "DrawRun"
include(":app")
include(":wearapp")

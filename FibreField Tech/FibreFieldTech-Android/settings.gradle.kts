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
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "FibreFieldTech"
include(":app")

// Core modules
include(":core:common")
include(":core:database")
include(":core:network")
include(":core:design")
// include(":core:ai")  // Temporarily disabled due to plugin conflicts

// Domain modules
include(":domain:authentication")
include(":domain:installation")
include(":domain:drops")
include(":domain:activation")
include(":domain:remediation")

// Feature modules
include(":feature:authentication")
include(":feature:installation")
include(":feature:drops")
include(":feature:activation")
include(":feature:remediation")

// Infrastructure modules
include(":infrastructure:sync")
include(":infrastructure:location")
include(":infrastructure:security")
rootProject.name = "invoice"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://packages.confluent.io/maven/")
    }
}

include(":commons")
include(":gateway")
include(":auth")
include(":account")
include(":catalog")
include(":invoice")

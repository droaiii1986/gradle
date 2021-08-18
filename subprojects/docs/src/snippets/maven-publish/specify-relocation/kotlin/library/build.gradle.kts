plugins {
    id("java-library")
    id("maven-publish")
}

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            url = uri("${rootProject.buildDir}/repo") // change to point to your repo, e.g. http://my.org/repo
        }
    }
}

dependencies {
    api("org.slf4j:slf4j-api:1.7.10")
}

// tag::specify-relocation[]
publishing {
    publications {
        // ... artifact publications

        // Specify relocation POM
        create<MavenPublication>("relocation") {
            pom {
                // Old artifact coordinates
                groupId = "com.example"
                artifactId = "lib"
                version = "2.0.0"

                distributionManagement {
                    relocation {
                        // New artifact coordinates
                        groupId.set("com.new-example")
                        artifactId.set("lib")
                        version.set("2.0.0")
                        message.set("groupId has been changed")
                    }
                }
            }
        }
    }
}
// end::specify-relocation[]

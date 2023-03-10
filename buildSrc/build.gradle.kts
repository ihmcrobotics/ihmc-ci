import com.gradle.publish.MavenCoordinates

plugins {
   `kotlin-dsl`
   id("com.gradle.plugin-publish") version "1.1.0"
}

group = "us.ihmc"
version = "7.9"

repositories {
   mavenCentral()
}

dependencies {
   api("org.junit.platform:junit-platform-console:1.9.2")
   api("org.junit.jupiter:junit-jupiter-engine:5.9.2")
   api("com.github.kittinunf.fuel:fuel:2.3.1")
   api("org.json:json:20220924")
}

val pluginVcsUrl = "https://github.com/ihmcrobotics/ihmc-ci"

gradlePlugin {
   plugins.register(project.name) {
      id = project.group as String + "." + project.name
      implementationClass = "us.ihmc.ci.IHMCCIPlugin"
      displayName = "IHMC CI"
      description = "Gradle plugin for running groups of tests with varied runtime requirements."
   }
}

import com.gradle.publish.MavenCoordinates

plugins {
   `kotlin-dsl`
   id("com.gradle.plugin-publish") version "1.1.0"
}

group = "us.ihmc"
version = "7.7"

repositories {
   mavenCentral()
}

dependencies {
   api("org.junit.platform:junit-platform-console:1.9.2")
   api("org.junit.jupiter:junit-jupiter-engine:5.9.2")
   api("com.github.kittinunf.fuel:fuel:2.3.1")
   api("org.json:json:20220924")
}

val pluginDisplayName = "IHMC CI"
val pluginDescription = "Gradle plugin for running groups of tests with varied runtime requirements."
val pluginVcsUrl = "https://github.com/ihmcrobotics/ihmc-ci"
val pluginTags = listOf("ci", "continuous", "integration", "ihmc", "robotics").filterNotNull()

gradlePlugin {
   plugins.register(project.name) {
      id = project.group as String + "." + project.name
      implementationClass = "us.ihmc.ci.IHMCCIPlugin"
      displayName = pluginDisplayName
      description = pluginDescription
   }
}

pluginBundle {
   website = pluginVcsUrl
   vcsUrl = pluginVcsUrl
   description = pluginDescription
   tags = pluginTags.values.flatten()
}


import com.gradle.publish.MavenCoordinates

plugins {
   `kotlin-dsl`
   `java-gradle-plugin`
   `maven-publish`
   id("com.gradle.plugin-publish") version "0.18.0"
}

group = "us.ihmc"
version = "7.5"

repositories {
   mavenCentral()
   jcenter()
}

dependencies {
   api("org.junit.platform:junit-platform-console:1.8.2")
   api("org.junit.jupiter:junit-jupiter-engine:5.8.2")
   api("com.github.kittinunf.fuel:fuel:2.3.1")
   api("org.json:json:20211205")
}

val pluginDisplayName = "IHMC CI"
val pluginDescription = "Gradle plugin for running groups of tests with varied runtime requirements."
val pluginVcsUrl = "https://github.com/ihmcrobotics/ihmc-ci"
val pluginTags = listOf("ci", "continuous", "integration", "ihmc", "robotics")

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
   tags = pluginTags

   plugins.getByName(project.name) {
      id = project.group as String + "." + project.name
      version = project.version as String
      displayName = pluginDisplayName
      description = pluginDescription
      tags = pluginTags
   }

   mavenCoordinates(closureOf<MavenCoordinates> {
      groupId = project.group as String
      artifactId = project.name
      version = project.version as String
   })
}
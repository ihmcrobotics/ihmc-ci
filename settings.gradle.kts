buildscript {
   repositories {
      maven { url = uri("https://plugins.gradle.org/m2/") }
      mavenLocal()
   }
   dependencies {
      classpath("us.ihmc:ihmc-build:0.23.0")
   }
}

/** Browse source at https://github.com/ihmcrobotics/ihmc-build */
val ihmcSettingsConfigurator = us.ihmc.build.IHMCSettingsConfigurator(settings, logger, extra)
ihmcSettingsConfigurator.checkRequiredPropertiesAreSet()
ihmcSettingsConfigurator.configureExtraSourceSets()

// includeBuild("src/junitfive-test/builds/categories")  // for testing discovery of external classpath tests

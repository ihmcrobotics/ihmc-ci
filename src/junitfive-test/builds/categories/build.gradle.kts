plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.log-tools-plugin") version "0.6.3"
   id("us.ihmc.ihmc-ci") version "7.6"
   id("us.ihmc.ihmc-cd") version "1.23"
}

ihmc {
   group = "us.ihmc"
   version = "0.1.0"
   vcsUrl = "https://your.vcs/url"
   openSource = true

   configureDependencyResolution()
   configurePublications()
}

//categories.configure("all") {
//
//}

//ihmc.sourceSetProject("test").tasks.named("test", Test::class.java) {
//
//}

mainDependencies {
   api("org.apache.commons:commons-lang3:3.12.0")
}

testDependencies {
}

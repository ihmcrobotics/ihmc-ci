plugins {
   id("us.ihmc.ihmc-build")
   id("us.ihmc.ihmc-ci")
}

ihmc {
   group = "us.ihmc"
   version = "4.25" // not released; only for tests
   vcsUrl = "https://github.com/ihmcrobotics/ihmc-ci"
   openSource = true
   maintainer = "Duncan Calvert"

   configureDependencyResolution()
   configurePublications()
}

categories.configure("fast").doFirst = {
   // scs.showGui() // this is how you would configure SCS as part of a test
   println("TEST FAST CONFIGURE")
}

categories.configure("allocation")

println(junit.jupiterVersion)
println(junit.jupiterApi())
println(allocation.instrumenter())

junitfiveTestDependencies {
   api("us.ihmc:log-tools:0.6.3")
   api("us.ihmc:ihmc-commons:0.30.5")

   // api("us.ihmc:categories-test:source")  // for testing discovery of external classpath tests
}

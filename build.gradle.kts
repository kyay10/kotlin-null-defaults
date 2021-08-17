buildscript {
  extra["kotlin_plugin_id"] = "io.github.kyay10.kotlin-null-defaults"
}
plugins {
  `maven-publish`
  kotlin("jvm") version Dependencies.kotlin apply false
  id("org.jetbrains.dokka") version "0.10.1" apply false
  id("com.gradle.plugin-publish") version "0.13.0" apply false
  id("com.github.gmazzo.buildconfig") version "3.0.0" apply false
}
val rootGroup = "io.github.kyay10"
group = rootGroup
val rootVersion = "0.1.1"
version = rootVersion
val rootName = name
subprojects {
  apply(plugin = "org.gradle.maven-publish")
  repositories {
    mavenCentral()
    jcenter()
  }
  // Naming scheme used by jitpack
  group = "$rootGroup.$rootName"
}

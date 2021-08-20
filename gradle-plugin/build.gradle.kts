@file:Suppress("UnstableApiUsage")

import com.gradle.publish.MavenCoordinates
import groovy.lang.Closure
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.utils.addToStdlib.cast

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig")
  id("com.gradle.plugin-publish")
  id("convention.publication")
}



dependencies {
  implementation(kotlin("stdlib"))
  implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
  val project = project(":kotlin-plugin")
  packageName(project.group.toString().replace("-", ""))
  buildConfigField(
    "String",
    "KOTLIN_PLUGIN_ID",
    "\"${rootProject.extra["kotlin_plugin_id"].toString().replace("-", "")}\""
  )
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
  val preludeProject = project(":prelude")
  buildConfigField("String", "PRELUDE_LIBRARY_GROUP", "\"${preludeProject.group}\"")
  buildConfigField("String", "PRELUDE_LIBRARY_NAME", "\"${preludeProject.name}\"")
  buildConfigField("String", "PRELUDE_LIBRARY_VERSION", "\"${preludeProject.version}\"")

}

java {
  withSourcesJar()
  withJavadocJar()
}

tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "1.8"
}
val pluginDescription =
  "Kotlin compiler plugin that allows Java callers to use null in place of default parameters"
val pluginName = "kotlin-null-defaults"
val pluginDisplayName = "Kotlin Null Defaults compiler plugin"
gradlePlugin {
  plugins {
    create(pluginName) {
      id = "io.github.kyay10.kotlin-null-defaults"
      displayName = pluginDisplayName
      description = pluginDescription
      implementationClass = "io.github.kyay10.kotlinnulldefaults.NullDefaultsGradlePlugin"
    }
  }
}
pluginBundle {
  website = "https://github.com/kyay10/kotlin-null-defaults"
  vcsUrl = website
  description = pluginDescription

  version = "0.1.1"
  (plugins) {
    pluginName {
      displayName = pluginDisplayName
      tags = listOf(
        "kotlin"
      )
      version = "0.1.1"
    }
  }
  val mavenCoordinatesConfiguration = { coords: MavenCoordinates ->
    coords.groupId = group.cast()
    coords.artifactId = name
    coords.version = version.cast()
  }

  mavenCoordinates(mavenCoordinatesConfiguration)
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  freeCompilerArgs = listOf("-Xinline-classes")
}

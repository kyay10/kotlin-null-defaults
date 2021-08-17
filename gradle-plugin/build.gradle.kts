import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig")
  id("com.gradle.plugin-publish")
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
      id = "com.github.kyay10.kotlin-null-defaults"
      displayName = pluginDisplayName
      description = pluginDescription
      implementationClass = "com.github.kyay10.kotlinnulldefaults.NullDefaultsGradlePlugin"
    }
  }
}
pluginBundle {
  website = "https://github.com/kyay10/kotlin-null-defaults"
  vcsUrl = website
  description = pluginDescription

  version = "0.1.0"
  (plugins) {
    pluginName {
      displayName = pluginDisplayName
      tags = listOf(
        "kotlin"
      )
      version = "0.1.0"
    }
  }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
  freeCompilerArgs = listOf("-Xinline-classes")
}

@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("org.jetbrains.dokka")
  id("convention.publication")
}

dependencies {
  implementation("org.ow2.asm:asm:9.1")
  compileOnly("org.jetbrains.kotlin:kotlin-compiler")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  kapt("com.google.auto.service:auto-service:1.0-rc7")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")

  // Needed for running tests since the tests inherit out classpath
  implementation(project(":prelude"))
}

val syncSource = tasks.register<Sync>("syncSource") {
  from(project(":kotlin-plugin").sourceSets.main.get().allSource)
  into("src/main/kotlin")
  filter {
    // Replace shadowed imports from plugin module
    it.replace("import org.jetbrains.kotlin.com.intellij.", "import com.intellij.")
      .replace("import org.jetbrains.kotlin.com.google.", "import com.google.")
  }
}

tasks.withType<KotlinCompile> {
  dependsOn("syncSource")
  kotlinOptions.jvmTarget = "1.8"
  kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

java {
  withSourcesJar()
  withJavadocJar()
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}

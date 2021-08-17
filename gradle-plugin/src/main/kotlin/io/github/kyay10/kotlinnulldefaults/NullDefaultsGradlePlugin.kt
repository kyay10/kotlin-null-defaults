/*
 * Copyright (C) 2020 Brian Norman
 * Copyright (C) 2021 Youssef Shoaib
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package io.github.kyay10.kotlinnulldefaults


import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class NullDefaultsGradlePlugin : KotlinCompilerPluginSupportPlugin {
  override fun apply(target: Project): Unit = with(target) {
    extensions.create("nullDefaults", NullDefaultsGradleExtension::class.java)
  }

  override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true

  override fun getCompilerPluginId(): String = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_ID

  override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
    groupId = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_GROUP,
    artifactId = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_NAME,
    version = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_VERSION
  )

  override fun getPluginArtifactForNative(): SubpluginArtifact = SubpluginArtifact(
    groupId = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_GROUP,
    artifactId = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_NAME + "-native",
    version = io.github.kyay10.kotlinnulldefaults.BuildConfig.KOTLIN_PLUGIN_VERSION
  )

  override fun applyToCompilation(
    kotlinCompilation: KotlinCompilation<*>
  ): Provider<List<SubpluginOption>> {
    val project = kotlinCompilation.target.project
    val extension = project.extensions.getByType(NullDefaultsGradleExtension::class.java)

    val sourceSetName =
      kotlinCompilation.compilationName

    kotlinCompilation.dependencies {
      implementation("${io.github.kyay10.kotlinnulldefaults.BuildConfig.PRELUDE_LIBRARY_GROUP}:${io.github.kyay10.kotlinnulldefaults.BuildConfig.PRELUDE_LIBRARY_NAME}:${io.github.kyay10.kotlinnulldefaults.BuildConfig.PRELUDE_LIBRARY_VERSION}")
    }

    return project.provider {
      listOf()
    }
  }
}

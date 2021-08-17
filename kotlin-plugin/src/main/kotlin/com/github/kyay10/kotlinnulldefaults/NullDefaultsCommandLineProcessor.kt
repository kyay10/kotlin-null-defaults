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

package com.github.kyay10.kotlinnulldefaults

import com.github.kyay10.kotlinnulldefaults.utils.OptionCommandLineProcessor
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

@AutoService(CommandLineProcessor::class)
class NullDefaultsCommandLineProcessor : CommandLineProcessor by Companion {
  companion object : OptionCommandLineProcessor(BuildConfig.KOTLIN_PLUGIN_ID)
}

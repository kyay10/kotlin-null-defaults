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

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.*
import java.lang.reflect.InvocationTargetException
import kotlin.system.measureTimeMillis
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NullDefaultsPluginTest {
  val outStream = ByteArrayOutputStream()
  val sampleFiles = mutableListOf<SourceFile>()
  lateinit var compiledSamples: KotlinCompilation.Result

  @BeforeAll
  fun setupSampleFiles() {
    val sampleJvmMainDirectory = File(BuildConfig.SAMPLE_JVM_MAIN_PATH)
    sampleFiles.addAll(sampleJvmMainDirectory.listFilesRecursively { it.extension == "kt" || it.extension == "java" }
      .map { SourceFile.fromPath(it) })
    println(
      "Kotlin Sample Compilation took ${
        measureTimeMillis {
          compiledSamples = compileSources(sampleFiles, outStream)
        }
      } milliseconds"
    )
  }

  @Test
  fun `Original Example`() {
    outStream.writeTo(System.out)
    val myOut = ByteArrayOutputStream()
    val printStream = PrintStream(myOut, false)
    System.setOut(printStream)
    runMain(compiledSamples, "MyClassKt")
    printStream.flush()
    val printed = myOut.toString()
    assertEquals(KotlinCompilation.ExitCode.OK, compiledSamples.exitCode)
    assertEquals(
      """
        hello.world.org
        2
        hello.world.org/2
        MyClass(name=, addresses=[], optionalConfig=null)
        MyClass(name=, addresses=[Hello, World], optionalConfig=null)
        MyClass(name=test, addresses=[], optionalConfig=Configuration(data=foo/bazzz))
        Configuration(data=42)""".trimIndent().trim()
      , printed.trim())
  }
}

fun compile(
  sourceFiles: List<SourceFile>,
  outputStream: OutputStream,
  plugin: ComponentRegistrar = NullDefaultsComponentRegistrar(),
  commandLineProcessor: CommandLineProcessor = NullDefaultsCommandLineProcessor(),
  className: String = "MainKt",
): KotlinCompilation.Result {
  val result = compileSources(sourceFiles, outputStream, plugin, commandLineProcessor)
  runMain(result, className)
  return result
}

private fun runMain(
  result: KotlinCompilation.Result,
  className: String = "MainKt"
) {
  val kClazz = result.classLoader.loadClass(className)
  val main = kClazz.declaredMethods.single { it.name == "main" && it.parameterCount == 0 }
  try {
    main.invoke(null)
  } catch (e: InvocationTargetException) {
    throw e.targetException!!
  }
}

private fun compileSources(
  sourceFiles: List<SourceFile>,
  outputStream: OutputStream,
  plugin: ComponentRegistrar = NullDefaultsComponentRegistrar(),
  commandLineProcessor: CommandLineProcessor = NullDefaultsCommandLineProcessor()
) = KotlinCompilation().apply {
  sources = sourceFiles
  useIR = true
  compilerPlugins = listOf(plugin)
  commandLineProcessors = listOf(commandLineProcessor)
  inheritClassPath = true
  messageOutputStream = outputStream
  verbose = true
  kotlincArguments = kotlincArguments + "-Xallow-kotlin-package"
}.compile()

fun compile(
  sourceFile: SourceFile,
  outputStream: OutputStream,
  plugin: ComponentRegistrar = NullDefaultsComponentRegistrar(),
  commandLineProcessor: CommandLineProcessor = NullDefaultsCommandLineProcessor()
): KotlinCompilation.Result {
  return compile(listOf(sourceFile), outputStream, plugin, commandLineProcessor)
}

fun File.listFilesRecursively(filter: FileFilter): List<File> =
  listOf(this).listFilesRecursively(filter, mutableListOf())

tailrec fun List<File>.listFilesRecursively(
  filter: FileFilter,
  files: MutableList<File> = mutableListOf()
): List<File> {
  val dirs = mutableListOf<File>()
  for (file in this) {
    val filteredFiles = file.listFiles(filter) ?: continue
    files.addAll(filteredFiles)
    dirs.addAll(file.listFiles { it: File -> it.isDirectory } ?: continue)
  }
  return dirs.ifEmpty { return files }.listFilesRecursively(filter, files)
}


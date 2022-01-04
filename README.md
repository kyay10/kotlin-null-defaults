# kotlin-null-defaults

![Maven Central](https://img.shields.io/maven-central/v/io.github.kyay10.kotlin-null-defaults/kotlin-plugin?color=gree) (Compiler plugin)
![Maven Central](https://img.shields.io/maven-central/v/io.github.kyay10.kotlin-null-defaults/gradle-plugin?color=gree) (Gradle Plugin)

[![](https://jitpack.io/v/kyay10/kotlin-null-defaults.svg)](https://jitpack.io/#kyay10/kotlin-null-defaults)

[![Gradle Plugin Portal](https://img.shields.io/maven-metadata/v?color=gree&label=gradlePluginPortal&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio%2Fgithub%2Fkyay10%2Fkotlin-null-defaults%2Fio.github.kyay10.kotlin-null-defaults.gradle.plugin%2Fmaven-metadata.xml)](https://plugins.gradle.org/plugin/io.github.kyay10.kotlin-null-defaults)

A Kotlin compiler plugin that allows Java callers to pass in `null` for default parameters by automatically checking for
nulls and substituting the default values .

In short, this plugin, through annotating any relevant declarations with @NullDefaults, allows you to treat your default
parameters as if they were nullable. The annotation can be applied to a whole class (for constructors), a specific
function/constructor, or even individual parameters.

The main use-case being supporting legacy Java code that expects being able to pass in null for certain parameters and
having that being handled on its own. Note that this plugin specifically doesn't allow Kotlin callers to do the same
thing due to the ease of use and idiomacy of default parameters in Kotlin and the fact that such a feature would go
against most established conventions. However, in the future the plugin could support this limited use-case behind a
flag.

Use with care!
Note: This plugin was created
using [Brian Norman's Kotlin IR Plugin Template](https://github.com/bnorm/kotlin-ir-plugin-template) and from guidance
from his wonderful article
series [Writing Your Second Kotlin Compiler Plugin](https://blog.bnorm.dev/writing-your-second-compiler-plugin-part-1) (
seriously like the articles were immensely helpful when I just knew absolutely nothing about IR)
and it was created originally as part of the discussion
on [Kotlin Discussions: Feature Request: Constructor parameter default value when receiving null input](https://discuss.kotlinlang.org/t/feature-request-constructor-parameter-default-value-when-receiving-null-input/22704)
to support the OP's use-case.

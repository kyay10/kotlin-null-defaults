package com.github.kyay10.kotlinnulldefaults.utils

import java.lang.reflect.Field

inline val Class<*>.allDeclaredFields: Sequence<Field>
  get() =
    generateSequence(this) { it.superclass }
      .map { it.declaredFields }
      .flatMap { it.toList() }

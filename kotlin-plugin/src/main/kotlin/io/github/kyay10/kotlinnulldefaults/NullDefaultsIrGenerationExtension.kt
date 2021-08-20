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

package io.github.kyay10.kotlinnulldefaults

import io.github.kyay10.kotlinnulldefaults.utils.*
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.irBlockBody
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.COPY_NAME
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.buildVariable
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfNull
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrReturnableBlockSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.util.constructedClass
import org.jetbrains.kotlin.ir.util.deepCopyWithSymbols
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.cast

val NULL_DEFAULTS_FQNAME = FqName("io.github.kyay10.kotlinnulldefaults.NullDefaults")

@Suppress("unused")
class NullDefaultsIrGenerationExtension(
  private val project: Project,
  private val messageCollector: MessageCollector,
  private val compilerConfig: CompilerConfiguration
) : IrGenerationExtension {
  @OptIn(ExperimentalStdlibApi::class, ObsoleteDescriptorBasedAPI::class)
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

    moduleFragment.lowerWith(object : IrFileTransformerVoidWithContext(pluginContext) {
      var parameterToDefaultValue: MutableMap<IrValueSymbol, IrExpression> = mutableMapOf()

      override fun visitClassNew(declaration: IrClass): IrStatement {
        val oldMap = parameterToDefaultValue.toMutableMap()
        val result = super.visitClassNew(declaration)
        declaration.transformChildrenVoid(object : IrElementTransformerVoidWithContext() {
          override fun visitGetValue(expression: IrGetValue): IrExpression {
            return parameterToDefaultValue[expression.symbol]?.deepCopyWithSymbols()
              ?.takeIf {
                expression.symbol.owner.parent != currentFunction?.irElement
              } ?: super.visitGetValue(expression)
          }
        })
        parameterToDefaultValue = oldMap
        return result
      }

      override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val isClassAnnotated = declaration.safeAs<IrConstructor>()?.constructedClass?.hasNullDefaultsAnnotation == true
        val isFunctionAnnotated = declaration.hasNullDefaultsAnnotation
        val isCopyFun =
          declaration.origin == IrDeclarationOrigin.GENERATED_DATA_CLASS_MEMBER && declaration.name == COPY_NAME
        val isCopyWhichIsIndirectlyAnnotated = isCopyFun && declaration.parent.safeAs<IrClass>().let { clazz ->
          clazz?.hasNullDefaultsAnnotation == true || clazz?.primaryConstructor?.hasNullDefaultsAnnotation == true
        }
        val areAllParametersEffectivelyAnnotated =
          isClassAnnotated || isFunctionAnnotated || isCopyWhichIsIndirectlyAnnotated
        if (areAllParametersEffectivelyAnnotated || declaration.valueParameters.any { it.hasNullDefaultsAnnotation }) {
          declaration.body =
            declarationIrBuilder.irBlockBody(declaration) {
              val functionBody = declaration.body
              if (functionBody is IrBlockBody || functionBody is IrExpressionBody) {
                val parameterToParameterRedeclaration: MutableMap<IrValueSymbol, IrValueSymbol> = mutableMapOf()
                val parameterSubstitutionTransformer = object : IrElementTransformerVoid() {
                  override fun visitGetValue(expression: IrGetValue): IrExpression {
                    return parameterToDefaultValue[expression.symbol]?.deepCopyWithSymbols(declaration)
                      ?: super.visitGetValue(expression)
                  }
                }
                val parameterRedeclarationTransformer = object : IrElementTransformerVoid() {
                  override fun visitGetValue(expression: IrGetValue): IrExpression {
                    return parameterToParameterRedeclaration[expression.symbol]?.let { irGet(it.owner) }
                      ?: super.visitGetValue(expression)
                  }
                }
                for (parameter in declaration.valueParameters) {
                  val defaultValue = parameter.defaultValue
                  if ((areAllParametersEffectivelyAnnotated || parameter.hasNullDefaultsAnnotation) && defaultValue != null) {
                    val parameterSubstitution: IrExpression = irIfNull(
                      parameter.type,
                      irGet(parameter),
                      defaultValue.expression.deepCopyWithSymbols(declaration).apply {
                        transformVoid(
                          parameterSubstitutionTransformer
                        )
                      },
                      irGet(parameter)
                    )
                    val parameterSubstitutionInBody: IrExpression = irIfNull(
                      parameter.type,
                      irGet(parameter),
                      defaultValue.expression.deepCopyWithSymbols(declaration).apply {
                        transformChildrenVoid(
                          parameterRedeclarationTransformer
                        )
                      },
                      irGet(parameter)
                    )

                    defaultValue.expression = defaultValue.expression.transformVoid(parameterSubstitutionTransformer)

                    val parameterRedeclaration = buildVariable(
                      declaration,
                      UNDEFINED_OFFSET,
                      UNDEFINED_OFFSET,
                      IrDeclarationOrigin.DEFINED,
                      Name.identifier("${parameter.name}-actual"),
                      parameter.type
                    ).apply {
                      initializer = parameterSubstitutionInBody.deepCopyWithSymbols(declaration)
                    }
                    +parameterRedeclaration
                    parameter.type = parameter.type.makeNullable()
                    parameterToDefaultValue[parameter.symbol] = parameterSubstitution.deepCopyWithSymbols(declaration)
                    parameterToParameterRedeclaration[parameter.symbol] = parameterRedeclaration.symbol
                  }
                }
                for (statement in functionBody.statements) {
                  +(statement.apply {
                    transformChildrenVoid(parameterRedeclarationTransformer)
                  })
                }
              }
            }
        }
        return super.visitFunctionNew(declaration)
      }
    })
  }
}


private val IrAnnotationContainer.hasNullDefaultsAnnotation: Boolean
  get() =
    annotations.any { it.type.classFqName == NULL_DEFAULTS_FQNAME }


fun IrExpression.extractFromReturnIfNeeded(): IrExpression =
  if (this is IrReturn && this.returnTargetSymbol is IrReturnableBlockSymbol) value.lastElement().cast<IrExpression>()
    .extractFromReturnIfNeeded() else this

fun IrExpression.calculatePredeterminedEqualityIfPossible(context: IrPluginContext): Boolean? {
  val trueElement = lastElement().cast<IrExpression>().extractFromReturnIfNeeded()
  if (trueElement is IrConst<*> && trueElement.kind == IrConstKind.Boolean) return trueElement.cast<IrConst<Boolean>>().value
  if (trueElement is IrCall && (trueElement.symbol == context.irBuiltIns.eqeqSymbol || trueElement.symbol == context.irBuiltIns.eqeqeqSymbol)) {
    val lhs = trueElement.getValueArgument(0)
    val rhs = trueElement.getValueArgument(1)
    if (lhs is IrGetEnumValue && rhs is IrGetEnumValue) return lhs.symbol == rhs.symbol
    if (lhs is IrConst<*> && rhs is IrConst<*>) return lhs.value == rhs.value
  }
  return null
}


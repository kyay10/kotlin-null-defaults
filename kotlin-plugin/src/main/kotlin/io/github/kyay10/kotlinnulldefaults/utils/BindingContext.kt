@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package io.github.kyay10.kotlinnulldefaults.utils

import org.jetbrains.kotlin.backend.common.CodegenUtil
import org.jetbrains.kotlin.codegen.JvmCodegenUtil
import org.jetbrains.kotlin.codegen.getCallLabelForLambdaArgument
import org.jetbrains.kotlin.codegen.isValueArgumentForCallToMethodWithTypeCheckBarrier
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.js.translate.utils.BindingUtils
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.synthetics.findClassDescriptor
import org.jetbrains.kotlin.psi2ir.isConstructorDelegatingToSuper
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.bindingContextUtil.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.smartcasts.getKotlinTypeForComparison
import org.jetbrains.kotlin.resolve.calls.util.*
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.expressions.KotlinTypeInfo
import org.jetbrains.kotlin.types.getElementType
import org.jetbrains.kotlin.util.slicedMap.ReadOnlySlice

private const val UNINITIALIZED_STRING = "UNINITIALIZED_STRING"

context(A) private fun <A> given(): A = this@A

// JvmCodegenUtil
context(BindingContext) val DeclarationDescriptor.isArgumentWhichWillBeInlined: Boolean
  get() = JvmCodegenUtil.isArgumentWhichWillBeInlined(
    given<BindingContext>(),
    this
  )
context(BindingContext) val VariableDescriptorWithAccessors.propertyDelegateType: KotlinType?
  get() = JvmCodegenUtil.getPropertyDelegateType(
    this,
    given<BindingContext>()
  )

// BindingUtils
context(BindingContext) val KtPureClassOrObject.classDescriptor: ClassDescriptor
  get() = BindingUtils.getClassDescriptor(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtDeclarationWithBody.functionDescriptor: FunctionDescriptor
  get() = BindingUtils.getFunctionDescriptor(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtProperty.propertyDescriptor: PropertyDescriptor
  get() = BindingUtils.getPropertyDescriptor(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtPureClassOrObject.hasAncestorClass: Boolean
  get() = BindingUtils.hasAncestorClass(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtTypeReference.type: KotlinType
  get() = BindingUtils.getTypeByReference(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtParameter.propertyDescriptorForConstructorParameter: PropertyDescriptor?
  get() = BindingUtils.getPropertyDescriptorForConstructorParameter(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtReferenceExpression.descriptor: DeclarationDescriptor?
  get() = BindingUtils.getDescriptorForReferenceExpression(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.isVariableReassignment: Boolean
  get() = BindingUtils.isVariableReassignment(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtOperationExpression.callableDescriptor: CallableDescriptor?
  get() = BindingUtils.getCallableDescriptorForOperationExpression(
    given<BindingContext>(),
    this
  )
context(BindingContext) val PsiElement.elementDescriptor: DeclarationDescriptor
  get() = BindingUtils.getDescriptorForElement(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.compileTimeValue: Any?
  get() = BindingUtils.getCompileTimeValue(
    given<BindingContext>(),
    this
  )

context(BindingContext) fun KtExpression.getCompileTimeValue(constant: CompileTimeConstant<*>): Any? =
  BindingUtils.getCompileTimeValue(given<BindingContext>(), this, constant)

context(BindingContext) val KtExpression.iteratorFunction: ResolvedCall<FunctionDescriptor>
  get() = BindingUtils.getIteratorFunction(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.nextFunction: ResolvedCall<FunctionDescriptor>
  get() = BindingUtils.getNextFunction(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.hasNextCallable: ResolvedCall<FunctionDescriptor>
  get() = BindingUtils.getHasNextCallable(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.kotlinType: KotlinType
  get() = BindingUtils.getTypeForExpression(
    given<BindingContext>(),
    this
  )

context(BindingContext) fun KtArrayAccessExpression.getResolvedCallForArrayAccess(isGet: Boolean): ResolvedCall<FunctionDescriptor> =
  BindingUtils.getResolvedCallForArrayAccess(
    given<BindingContext>(),
    this,
    isGet
  )

context(BindingContext) val KtArrayAccessExpression.resolvedCallForArrayAccess: ResolvedCall<FunctionDescriptor>
  get() = getResolvedCallForArrayAccess(false)
context(BindingContext) val KtPureClassOrObject.superCall: ResolvedCall<FunctionDescriptor>?
  get() = BindingUtils.getSuperCall(
    given<BindingContext>(),
    this
  )

// BindingContextUtils
context(BindingContext) val KtElement.extractVariableFromResolvedCall: VariableDescriptor?
  get() = BindingContextUtils.extractVariableFromResolvedCall(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtElement.extractVariableDescriptorFromReference: VariableDescriptor?
  get() = BindingContextUtils.extractVariableDescriptorFromReference(
    given<BindingContext>(),
    this
  )

context(BindingContext) operator fun <K : Any, V> ReadOnlySlice<K, V>.get(
  key: K,
  messageIfNull: String = UNINITIALIZED_STRING
): V =
  if (messageIfNull == UNINITIALIZED_STRING) BindingContextUtils.getNotNull(
    given<BindingContext>(),
    this,
    key
  ) else BindingContextUtils.getNotNull(given<BindingContext>(), this, key, messageIfNull)

context(BindingContext) val KtExpression.recordedTypeInfo: KotlinTypeInfo?
  get() = BindingContextUtils.getRecordedTypeInfo(
    this,
    given<BindingContext>()
  )
context(BindingContext) val KtExpression.isExpressionWithValidReference: Boolean
  get() = BindingContextUtils.isExpressionWithValidReference(
    this,
    given<BindingContext>()
  )
context(BindingContext) val DeclarationDescriptor.isCapturedInClosure: Boolean
  get() = BindingContextUtils.isCapturedInClosure(
    given<BindingContext>(),
    this
  )
context(BindingContext) val DeclarationDescriptor.isCapturedInClosureWithExactlyOnceEffect: Boolean
  get() = BindingContextUtils.isCapturedInClosureWithExactlyOnceEffect(
    given<BindingContext>(),
    this
  )
context(BindingContext) val DeclarationDescriptor.isBoxedLocalCapturedInClosure: Boolean
  get() = BindingContextUtils.isBoxedLocalCapturedInClosure(
    given<BindingContext>(),
    this
  )
context(BindingContext) val ConstructorDescriptor.delegationConstructorCall: ResolvedCall<ConstructorDescriptor>?
  get() = BindingContextUtils.getDelegationConstructorCall(
    given<BindingContext>(),
    this
  )

// CompileTimeConstantUtils
context(BindingContext) fun KtExpression?.canBeReducedToBooleanConstant(expectedValue: Boolean): Boolean =
  CompileTimeConstantUtils.canBeReducedToBooleanConstant(this, given<BindingContext>(), expectedValue)

// ModifiersChecker
context(BindingContext) fun KtModifierListOwner?.resolveMemberModalityFromModifiers(
  defaultModality: Modality,
  containingDescriptor: DeclarationDescriptor
): Modality =
  ModifiersChecker.resolveMemberModalityFromModifiers(
    this,
    defaultModality,
    given<BindingContext>(),
    containingDescriptor
  )

context(BindingContext) fun KtModifierListOwner?.resolveModalityFromModifiers(
  defaultModality: Modality,
  containingDescriptor: DeclarationDescriptor,
  allowSealed: Boolean
): Modality = ModifiersChecker.resolveModalityFromModifiers(
  this,
  defaultModality,
  given<BindingContext>(),
  containingDescriptor,
  allowSealed
)

// InlineUtil
context(BindingContext) fun DeclarationDescriptor.checkNonLocalReturnUsage(
  containingFunctionDescriptor: DeclarationDescriptor?,
  containingFunction: PsiElement?
) = InlineUtil.checkNonLocalReturnUsage(this, containingFunctionDescriptor, containingFunction, given<BindingContext>())

context(BindingContext) fun KtFunction.isInlinedArgument(checkNonLocalReturn: Boolean = false): Boolean =
  InlineUtil.isInlinedArgument(this, given<BindingContext>(), checkNonLocalReturn)

context(BindingContext) val KtFunction.isInlinedArgument: Boolean get() = isInlinedArgument()
context(BindingContext) val KtFunction.inlineArgumentDescriptor: ValueParameterDescriptor?
  get() = InlineUtil.getInlineArgumentDescriptor(
    this,
    given<BindingContext>()
  )

// RangeCodegenUtil
context(BindingContext) val KtForExpression.elementType: KotlinType get() = getElementType(this)

// CodegenUtil
context(BindingContext) fun KtExpression.getDelegatePropertyIfAny(classDescriptor: ClassDescriptor): PropertyDescriptor? =
  CodegenUtil.getDelegatePropertyIfAny(this, classDescriptor, given<BindingContext>())

context(BindingContext) val KtFile.memberDescriptorsToGenerate: List<MemberDescriptor>
  get() = CodegenUtil.getMemberDescriptorsToGenerate(
    this,
    given<BindingContext>()
  )
context(BindingContext) val KtSuperTypeListEntry.superClass: ClassDescriptor?
  get() = CodegenUtil.getSuperClassBySuperTypeListEntry(
    this,
    given<BindingContext>()
  )

context(BindingContext) fun KtWhenExpression.isExhaustive(isStatement: Boolean): Boolean =
  CodegenUtil.isExhaustive(given<BindingContext>(), this, isStatement)

context(BindingContext) val PropertyDescriptor?.isFinalPropertyWithBackingField: Boolean
  get() = CodegenUtil.isFinalPropertyWithBackingField(
    this,
    given<BindingContext>()
  )

// Various Extensions
context(BindingContext) val KtElement.isValueArgumentForCallToMethodWithTypeCheckBarrier: Boolean
  get() = isValueArgumentForCallToMethodWithTypeCheckBarrier(
    this,
    given<BindingContext>()
  )
context(BindingContext) val KtFunctionLiteral.callLabelForLambdaArgument: String?
  get() = getCallLabelForLambdaArgument(
    this,
    given<BindingContext>()
  )
context(BindingContext) val KtPureElement.classDescriptor: ClassDescriptor get() = findClassDescriptor(given<BindingContext>())
context(BindingContext) val KtSecondaryConstructor.isConstructorDelegatingToSuper: Boolean
  get() = isConstructorDelegatingToSuper(
    given<BindingContext>()
  )
context(BindingContext) val PropertyDescriptor.hasBackingField: Boolean get() = hasBackingField(given<BindingContext>())
context(BindingContext) val KtTypeElement.abbreviatedTypeOrType: KotlinType? get() = getAbbreviatedTypeOrType(given<BindingContext>())
context(BindingContext) val KtTypeReference.abbreviatedTypeOrType: KotlinType? get() = getAbbreviatedTypeOrType(given<BindingContext>())
context(BindingContext) val KtElement.enclosingDescriptor: DeclarationDescriptor
  get() = getEnclosingDescriptor(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtElement.enclosingFunctionDescriptor: FunctionDescriptor?
  get() = getEnclosingFunctionDescriptor(
    given<BindingContext>(),
    this
  )
context(BindingContext) val KtExpression.referenceTargets: Collection<DeclarationDescriptor>
  get() = getReferenceTargets(
    given<BindingContext>()
  )
context(BindingContext) val KtReturnExpression.targetFunction: KtCallableDeclaration? get() = getTargetFunction(given<BindingContext>())
context(BindingContext) val KtReturnExpression.targetFunctionDescriptor: FunctionDescriptor?
  get() = getTargetFunctionDescriptor(
    given<BindingContext>()
  )
context(BindingContext) val KtExpression.isUsedAsExpression: Boolean get() = isUsedAsExpression(given<BindingContext>())
context(BindingContext) val KtExpression.isUsedAsResultOfLambda: Boolean get() = isUsedAsResultOfLambda(given<BindingContext>())
context(BindingContext) val KtExpression.isUsedAsStatement: Boolean get() = isUsedAsStatement(given<BindingContext>())
context(BindingContext) fun KtCallElement.getArgumentByParameterIndex(index: Int): List<ValueArgument> =
  getArgumentByParameterIndex(index, given<BindingContext>())

context(BindingContext) val ResolvedCall<*>.hasThisOrNoDispatchReceiver: Boolean
  get() = hasThisOrNoDispatchReceiver(
    given<BindingContext>()
  )
context(BindingContext) val KtExpression.kotlinTypeForComparison: KotlinType? get() = getKotlinTypeForComparison(given<BindingContext>())
context(BindingContext) val KtElement.call: Call? get() = getCall(given<BindingContext>())
context(BindingContext) val KtElement.callWithAssert: Call get() = getCallWithAssert(given<BindingContext>())
context(BindingContext) val KtExpression.functionResolvedCallWithAssert: ResolvedCall<out FunctionDescriptor>
  get() = getFunctionResolvedCallWithAssert(
    given<BindingContext>()
  )

context(BindingContext) fun KtElement.getParentCall(strict: Boolean = true): Call? =
  getParentCall(given<BindingContext>(), strict)

context(BindingContext) val KtElement.parentCall: Call? get() = getParentCall()
context(BindingContext) fun KtElement?.getParentResolvedCall(strict: Boolean = true): ResolvedCall<out CallableDescriptor>? =
  getParentResolvedCall(given<BindingContext>(), strict)

context(BindingContext) val KtElement?.parentResolvedCall: ResolvedCall<out CallableDescriptor>? get() = getParentResolvedCall()
context(BindingContext) val KtExpression.propertyResolvedCallWithAssert: ResolvedCall<out PropertyDescriptor>
  get() = getPropertyResolvedCallWithAssert(
    given<BindingContext>()
  )
context(BindingContext) val Call?.resolvedCall: ResolvedCall<out CallableDescriptor>? get() = getResolvedCall(given<BindingContext>())
context(BindingContext) val KtElement?.resolvedCall: ResolvedCall<out CallableDescriptor>? get() = getResolvedCall(given<BindingContext>())
context(BindingContext) val KtElement.resolvedCallWithAssert: ResolvedCall<out CallableDescriptor>
  get() = getResolvedCallWithAssert(
    given<BindingContext>()
  )
context(BindingContext) val Call.resolvedCallWithAssert: ResolvedCall<out CallableDescriptor>
  get() = getResolvedCallWithAssert(
    given<BindingContext>()
  )
context(BindingContext) val KtExpression.typeThroughResolvingCall: KotlinType? get() = getType(given<BindingContext>())
context(BindingContext) val KtExpression.variableResolvedCallWithAssert: ResolvedCall<out VariableDescriptor>
  get() = getVariableResolvedCallWithAssert(
    given<BindingContext>()
  )

context(BindingContext) fun Call.hasUnresolvedArguments(statementFilter: StatementFilter): Boolean =
  hasUnresolvedArguments(given<BindingContext>(), statementFilter)

context(BindingContext) val KtExpression.constant: CompileTimeConstant<*>?
  get() = org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator.run {
    getConstant(
      this@constant,
      given<BindingContext>()
    )
  }

context(BindingContext) val KtExpression.possiblyErrorConstant: CompileTimeConstant<*>?
  get() = org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator.run {
    getPossiblyErrorConstant(
      this@possiblyErrorConstant,
      given<BindingContext>()
    )
  }

context(BindingContext) fun KtExpression.getCompileTimeConstant(
  takeUpConstValsAsConst: Boolean,
  shouldInlineConstVals: Boolean
): ConstantValue<*>? = org.jetbrains.kotlin.resolve.jvm.getCompileTimeConstant(
  this,
  given<BindingContext>(),
  takeUpConstValsAsConst,
  shouldInlineConstVals
)



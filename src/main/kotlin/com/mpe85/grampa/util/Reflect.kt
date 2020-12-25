package com.mpe85.grampa.util

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility
import kotlin.reflect.full.hasAnnotation

internal fun Method.isPublic() = Modifier.isPublic(modifiers)
internal fun Method.isProtected() = Modifier.isProtected(modifiers)
internal fun Method.isPublicOrProtected() = isPublic() || isProtected()
internal fun Method.isFinal() = Modifier.isFinal(modifiers)
internal fun Method.isSafeVarArgsRuleMethod() = isVarArgs && isAnnotationPresent(SafeVarargs::class.java)
internal fun Method.isStatic() = Modifier.isStatic(modifiers)

internal fun KFunction<*>.isPublic() = visibility == KVisibility.PUBLIC
internal fun KFunction<*>.isProtected() = visibility == KVisibility.PROTECTED
internal fun KFunction<*>.isPublicOrProtected() = isPublic() || isProtected()
internal fun KFunction<*>.isSafeVarArgsRuleFunction() = parameters.any { it.isVararg } && hasAnnotation<SafeVarargs>()

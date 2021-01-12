package com.mpe85.grampa.util

import java.lang.reflect.Method
import java.lang.reflect.Modifier.isFinal
import java.lang.reflect.Modifier.isProtected
import java.lang.reflect.Modifier.isPublic
import java.lang.reflect.Modifier.isStatic
import kotlin.reflect.KFunction
import kotlin.reflect.KVisibility.PROTECTED
import kotlin.reflect.KVisibility.PUBLIC

/**
 * Check if a [Method] is public.
 *
 * @return true if the method is public
 */
internal fun Method.isPublic() = isPublic(modifiers)

/**
 * Check if a [Method] is protected.
 *
 * @return true if the method is protected
 */
internal fun Method.isProtected() = isProtected(modifiers)

/**
 * Check if a [Method] is public or protected.
 *
 * @return true if the method is either public or protected
 */
internal fun Method.isPublicOrProtected() = isPublic() || isProtected()

/**
 * Check if a [Method] is final.
 *
 * @return true if the method is final
 */
internal fun Method.isFinal() = isFinal(modifiers)

/**
 * Check if a [Method] is static.
 *
 * @return true if the method is static
 */
internal fun Method.isStatic() = isStatic(modifiers)

/**
 * Check if a [KFunction] is public.
 *
 * @return true if the function is public
 */
internal fun KFunction<*>.isPublic() = visibility == PUBLIC

/**
 * Check if a [KFunction] is protected.
 *
 * @return true if the function is protected
 */
internal fun KFunction<*>.isProtected() = visibility == PROTECTED

/**
 * Check if a [KFunction] is public or protected.
 *
 * @return true if the function is either public or protected
 */
internal fun KFunction<*>.isPublicOrProtected() = isPublic() || isProtected()

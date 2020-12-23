package com.mpe85.grampa.util

import java.util.Objects.equals
import kotlin.reflect.KProperty1

/**
 * Check if an object equals another object.
 *
 * @param[T] The object type
 * @param[other] Another object
 * @param[superEquals] The super equals check
 * @param[properties] The properties used to check equality
 * @return true if this object equals the other object
 */
internal inline fun <reified T : Any> T.checkEquality(
  other: Any?,
  superEquals: (() -> Boolean),
  vararg properties: KProperty1<T, Any?> = emptyArray()
) = when {
  other === this -> true
  other !is T -> false
  !superEquals() -> false
  else -> properties.all { equals(it.get(this), it.get(other)) }
}

/**
 * Check if an object equals another object.
 *
 * @param[T] The object type
 * @param[other] Another object
 * @param[properties] The properties used to check equality
 * @return true if this object equals the other object
 */
internal inline fun <reified T : Any> T.checkEquality(
  other: Any?,
  vararg properties: KProperty1<T, Any?> = emptyArray()
) = checkEquality(other, { true }, *properties)

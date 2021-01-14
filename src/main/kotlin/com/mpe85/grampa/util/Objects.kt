package com.mpe85.grampa.util

import java.util.Objects.equals

/**
 * Check if an object equals another object.
 *
 * @param[T] The object type
 * @param[other] Another object
 * @param[superEquals] The super equals check
 * @param[properties] The object's properties used to check the equality
 * @return true if this object equals the other object
 */
internal inline fun <reified T : Any> T.checkEquality(
        other: Any?,
        superEquals: () -> Boolean = { true },
        vararg properties: (T) -> Any? = emptyArray()
) = when {
    other === this -> true
    other !is T -> false
    !superEquals() -> false
    else -> properties.all { equals(it(this), it(other)) }
}

/**
 * Stringify an object using a variable argument list of object properties.
 *
 * @param[properties] The object properties that should be used in the string representation.
 * @return The string representation of the object
 */
internal fun Any.stringify(vararg properties: Pair<String, Any?> = emptyArray()) = buildString {
    append("${this@stringify::class.simpleName}(")
    append(properties.joinToString(", ") { "${it.first}=${it.second}" })
    append(")")
}

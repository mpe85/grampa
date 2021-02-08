package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A string rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[string] A string
 */
public open class StringRule<T> constructor(private val string: String) : AbstractRule<T>() {

    override fun match(context: ParserContext<T>): Boolean {
        if (context.numberOfCharsLeft >= string.length) {
            return string == context.restOfInput.substring(0, string.length) && context.advanceIndex(string.length)
        }
        return false
    }

    override fun hashCode(): Int = hash(super.hashCode(), string)
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) }, { it.string })
    override fun toString(): String = stringify("string" to string)
}

/**
 * Create a rule from this string.
 *
 * @return A [StringRule]
 */
public fun <T> String.toRule(): Rule<T> = StringRule(this)

package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A string rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[string] A string
 */
internal class StringRule<T>(private val string: String) : AbstractRule<T>() {

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

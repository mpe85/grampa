package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.toLowerCase
import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A string rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[string] A string
 * @property[ignoreCase] Indicates if the case should be ignored
 */
class StringRule<T> @JvmOverloads constructor(string: String, private val ignoreCase: Boolean = false) :
    AbstractRule<T>() {

    private val string: String = if (ignoreCase) toLowerCase(string) else string

    override fun match(context: ParserContext<T>): Boolean {
        if (context.numberOfCharsLeft >= string.length) {
            val nextChars = context.inputBuffer.subSequence(context.currentIndex, context.currentIndex + string.length)
            return string == (if (ignoreCase) toLowerCase(nextChars.toString()) else nextChars.toString()) &&
                    context.advanceIndex(string.length)
        }
        return false
    }

    override fun hashCode() = hash(super.hashCode(), string, ignoreCase)
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.string }, { it.ignoreCase })
    override fun toString() = stringify("string" to string, "ignoreCase" to ignoreCase)
}

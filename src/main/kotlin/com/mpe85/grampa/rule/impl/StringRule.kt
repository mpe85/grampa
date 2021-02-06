package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.lang.UCharacter.toUpperCase
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
 * @property[ignoreCase] Indicates if the case should be ignored
 */
public class StringRule<T> @JvmOverloads constructor(
    private val string: String,
    private val ignoreCase: Boolean = false
) : AbstractRule<T>() {

    override fun match(context: ParserContext<T>): Boolean {
        if (context.numberOfCharsLeft >= string.length) {
            val nextChars = context.inputBuffer.subSequence(context.currentIndex, context.currentIndex + string.length)
            return if (ignoreCase) {
                toLowerCase(string) == toLowerCase(nextChars.toString())
                        || toUpperCase(string) == toUpperCase(nextChars.toString())
            } else {
                string == nextChars.toString()
            } && context.advanceIndex(string.length)
        }
        return false
    }

    override fun hashCode(): Int = hash(super.hashCode(), string, ignoreCase)
    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.string }, { it.ignoreCase })

    override fun toString(): String = stringify("string" to string, "ignoreCase" to ignoreCase)
}

/**
 * Create a rule from this string.
 *
 * @return A [StringRule]
 */
public fun <T> String.toRule(): Rule<T> = StringRule(this)

package com.mpe85.grampa.rule.impl

import com.ibm.icu.text.UTF16.StringComparator
import com.ibm.icu.text.UTF16.StringComparator.FOLD_CASE_DEFAULT
import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash
import kotlin.streams.asSequence

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

    private val comparator = StringComparator(true, ignoreCase, FOLD_CASE_DEFAULT)
    private val codePointCount = string.codePoints().count().toInt()

    override fun match(context: ParserContext<T>): Boolean {
        if (context.numberOfCharsLeft >= string.length) {
            val input = context.restOfInput.codePoints().asSequence().take(codePointCount)
                .fold(StringBuilder(), StringBuilder::appendCodePoint).toString()
            return comparator.compare(string, input) == 0 && context.advanceIndex(input.length)
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

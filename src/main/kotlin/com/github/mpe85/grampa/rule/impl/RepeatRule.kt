package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.util.UnboundedRange
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A repeat rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The rule to repeat
 * @property[min] The minimum number of cycles
 * @property[max] An optional maximum number of cycles
 */
public class RepeatRule<T>(private val rule: Rule<T>, private val min: Int, private val max: Int? = null) :
    AbstractRule<T>(rule) {

    init {
        require(min >= 0) { "A 'min' number must not be negative" }
        max?.let {
            require(it >= min) { "A 'max' number must not be lower than the 'min' number." }
        }
    }

    override fun match(context: ParserContext<T>): Boolean {
        var iterations = 0
        while (max == null || iterations < max) {
            if (!context.createChildContext(rule).run()) {
                break
            }
            iterations++
        }
        return iterations >= min
    }

    override fun hashCode(): Int = hash(super.hashCode(), min, max)
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) }, { it.min }, { it.max })
    override fun toString(): String = stringify("rule" to rule, "min" to min, "max" to max)
}

/**
 * Repeat a rule exactly n times.
 *
 * @param[rule] The rule to repeat
 * @return A [RepeatRule]
 */
public operator fun <T> Int.times(rule: Rule<T>): Rule<T> = RepeatRule(rule, this, this)

/**
 * Repeat a rule between n and m times.
 *
 * @param[rule] The rule to repeat
 * @return A [RepeatRule]
 */
public operator fun <T> IntRange.times(rule: Rule<T>): Rule<T> = RepeatRule(rule, first, last)

/**
 * Repeat a rule between n and m times where m may be unbounded.
 *
 * @param[rule] The rule to repeat
 * @return A [RepeatRule]
 */
public operator fun <T> UnboundedRange.times(rule: Rule<T>): Rule<T> = RepeatRule(rule, min, max)

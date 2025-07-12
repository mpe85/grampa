package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A repeat rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[rule] The rule to repeat
 * @param[min] The minimum number of cycles
 * @param[max] An optional maximum number of cycles
 * @author mpe85
 */
internal class RepeatRule<T>(rule: Rule<T>, private val min: Int, private val max: Int? = null) :
    AbstractRule<T>(rule) {

    init {
        require(min >= 0) { "A 'min' number must not be negative" }
        max?.let {
            require(it >= min) { "A 'max' number must not be lower than the 'min' number." }
        }
    }

    private val rule
        get() = checkNotNull(child)

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

    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.min }, { it.max })

    override fun toString(): String = stringify("rule" to rule, "min" to min, "max" to max)
}

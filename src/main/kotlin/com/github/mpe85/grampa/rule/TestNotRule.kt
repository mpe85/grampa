package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A predicate rule implementation that tests if its child rule does not match.
 *
 * @param[T] The type of the stack elements
 * @param[rule] The child rule to test
 * @author mpe85
 */
internal class TestNotRule<T>(rule: Rule<T>) : AbstractRule<T>(rule) {

    private val rule
        get() = checkNotNull(child)

    override val testRule: Boolean = true

    override fun match(context: ParserContext<T>): Boolean = !context.createChildContext(rule).run()

    override fun hashCode(): Int = hash(super.hashCode())

    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })

    override fun toString(): String = stringify("rule" to rule::class.simpleName)
}

package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A predicate rule implementation that tests if its child rule matches.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The child rule to test
 */
public class TestRule<T>(private var rule: Rule<T>) : AbstractRule<T>(rule) {

    override val testRule: Boolean = true

    override fun match(context: ParserContext<T>): Boolean {
        val currentIndex = context.currentIndex
        context.stack.takeSnapshot()
        if (context.createChildContext(rule).run()) {
            // reset current index and stack
            context.currentIndex = currentIndex
            context.stack.restoreSnapshot()
            return true
        }
        return false
    }

    override fun hashCode(): Int = hash(super.hashCode())
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })
    override fun toString(): String = stringify("rule" to rule::class.simpleName)
}

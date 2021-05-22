@file:JvmName("TestNotRules")

package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A predicate rule implementation that tests if its child rule does not match.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The child rule to test
 */
internal class TestNotRule<T>(private val rule: Rule<T>) : AbstractRule<T>(rule) {

    override val testRule: Boolean = true

    override fun match(context: ParserContext<T>): Boolean = !context.createChildContext(rule).run()

    override fun hashCode(): Int = hash(super.hashCode())
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })
    override fun toString(): String = stringify("rule" to rule::class.simpleName)
}

/**
 * Create a [TestNotRule] out of this rule.
 *
 * @return A [TestNotRule] with [this] as its child rule
 */
public fun <T> Rule<T>.testNot(): Rule<T> = TestNotRule(this)

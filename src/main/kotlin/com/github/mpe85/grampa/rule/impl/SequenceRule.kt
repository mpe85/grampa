@file:JvmName("SequenceRules")

package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.rule.AbstractRule
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A sequence rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
internal class SequenceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

    override fun match(context: ParserContext<T>): Boolean = children.all { context.createChildContext(it).run() }

    override fun hashCode(): Int = hash(super.hashCode())
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })
    override fun toString(): String = stringify("#children" to children.size)
}

/**
 * Create a [SequenceRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [SequenceRule] with [this] and [other] as its child rules
 */
public infix fun <T> Rule<T>.and(other: Rule<T>): Rule<T> = SequenceRule(listOf(this, other))

/**
 * Create a [SequenceRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [SequenceRule] with [this] and [other] as its child rules
 */
public operator fun <T> Rule<T>.plus(other: Rule<T>): Rule<T> = this and other

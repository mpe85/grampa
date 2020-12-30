package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.stringify

/**
 * A sequence rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
class SequenceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: ParserContext<T>) = children.all { c -> context.createChildContext(c).run() }

  override fun toString() = stringify("children" to children)

}

/**
 * Create a [SequenceRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [SequenceRule] with [this] and [other] as its child rules
 */
infix fun <T> Rule<T>.and(other: Rule<T>) = SequenceRule(listOf(this, other))

/**
 * Create a [SequenceRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [SequenceRule] with [this] and [other] as its child rules
 */
operator fun <T> Rule<T>.plus(other: Rule<T>) = this and other

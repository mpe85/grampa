package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.stringify

/**
 * A rule implementation that matches the first successful rule of its child rules.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
class FirstOfRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: ParserContext<T>) = children.any { c -> context.createChildContext(c).run() }

  override fun toString() = stringify("children" to children)

}

/**
 * Create a [FirstOfRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [FirstOfRule] with [this] and [other] as its child rules
 */
infix fun <T> Rule<T>.or(other: Rule<T>) = FirstOfRule(listOf(this, other))

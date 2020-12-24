package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.stringify

/**
 * A sequence rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
class SequenceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: RuleContext<T>) = children.all { c -> context.createChildContext(c).run() }

  override fun toString() = stringify("children" to children)

}

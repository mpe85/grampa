package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that matches the first successful rule of its child rules.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param rules a list of child rules
 */
class FirstOfRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: RuleContext<T>) = children.any { c -> context.createChildContext(c).run() }

}

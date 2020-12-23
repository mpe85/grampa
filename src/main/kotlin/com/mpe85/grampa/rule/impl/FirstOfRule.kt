package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that matches the first successful rule of its child rules.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
class FirstOfRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: RuleContext<T>) = children.any { c -> context.createChildContext(c).run() }

  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(FirstOfRule<*>::children)
  }

}

package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A sequence rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
class SequenceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

  override fun match(context: RuleContext<T>) = children.all { c -> context.createChildContext(c).run() }

  override fun hashCode() = kotlinHashCode(properties)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(SequenceRule<*>::children)
  }

}

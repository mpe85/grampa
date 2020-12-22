package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A predicate rule implementation that tests if its child rule matches.
 *
 * @author mpe85
 * @param T The type of the stack elements
 * @property rule The child rule to test
 */
class TestRule<T>(private var rule: Rule<T>) : AbstractRule<T>(rule) {

  override fun match(context: RuleContext<T>): Boolean {
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

  override val isPredicate = true

  override fun hashCode() = kotlinHashCode(properties)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(TestRule<*>::rule)
  }

}

package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A predicate rule implementation that tests if its child rule does not match.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The child rule to test
 */
class TestNotRule<T>(private val rule: Rule<T>) : AbstractRule<T>(rule) {

  override fun match(context: RuleContext<T>) = !context.createChildContext(rule).run()

  override val testRule get() = true
  
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(TestNotRule<*>::rule)
  }

}

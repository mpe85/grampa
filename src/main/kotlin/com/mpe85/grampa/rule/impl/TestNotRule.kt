package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.stringify

/**
 * A predicate rule implementation that tests if its child rule does not match.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The child rule to test
 */
class TestNotRule<T>(private val rule: Rule<T>) : AbstractRule<T>(rule) {

  override val testRule get() = true

  override fun match(context: RuleContext<T>) = !context.createChildContext(rule).run()

  override fun toString() = stringify(TestNotRule<T>::rule)

}

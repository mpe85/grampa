package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A predicate rule implementation that tests if its child rule does not match.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param rule the child rule to test
 */
class TestNotRule<T>(private val rule: Rule<T>) : AbstractRule<T>(rule) {

  override fun match(context: RuleContext<T>) = !context.createChildContext(rule).run()

  override fun isPredicate() = true

}

package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext

/**
 * A predicate rule implementation that tests if its child rule matches.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param rule the child rule to test
 */
class TestRule<T>(rule: Rule<T>) : AbstractRule<T>(rule) {

  override fun match(context: RuleContext<T>): Boolean {
    val currentIndex = context.currentIndex
    context.stack.takeSnapshot()
    if (context.createChildContext(child).run()) {
      // reset current index and stack
      context.currentIndex = currentIndex
      context.stack.restoreSnapshot()
      return true
    }
    // TODO Remove snapshot?
    return false
  }

  override fun isPredicate() = true

}

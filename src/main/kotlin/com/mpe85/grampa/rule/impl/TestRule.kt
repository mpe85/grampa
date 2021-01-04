package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A predicate rule implementation that tests if its child rule matches.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The child rule to test
 */
class TestRule<T>(private var rule: Rule<T>) : AbstractRule<T>(rule) {

  override val testRule get() = true

  override fun match(context: ParserContext<T>): Boolean {
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

  override fun hashCode() = hash(super.hashCode())
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) })
  override fun toString() = stringify("rule" to rule::class.simpleName)

}

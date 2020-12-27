package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.ParserContext
import com.mpe85.grampa.rule.Rule
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

  override fun match(context: ParserContext<T>) = !context.createChildContext(rule).run()

  override fun toString() = stringify("rule" to rule)

}

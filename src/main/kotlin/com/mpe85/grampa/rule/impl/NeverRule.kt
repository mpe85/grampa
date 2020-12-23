package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.stringify

/**
 * A rule implementation that never matches.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class NeverRule<T> : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = false

  override fun toString() = stringify()

}

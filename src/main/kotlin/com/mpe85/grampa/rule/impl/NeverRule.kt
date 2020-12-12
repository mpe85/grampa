package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that never matches.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
class NeverRule<T> : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = false

}

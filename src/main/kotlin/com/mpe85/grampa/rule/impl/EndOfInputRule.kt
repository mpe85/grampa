package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that matches the end of the input.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
class EndOfInputRule<T> : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = context.isAtEndOfInput

}

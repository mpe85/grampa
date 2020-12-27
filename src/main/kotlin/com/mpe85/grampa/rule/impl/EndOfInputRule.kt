package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.ParserContext
import com.mpe85.grampa.util.stringify

/**
 * A rule implementation that matches the end of the input.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class EndOfInputRule<T> : AbstractRule<T>() {

  override fun match(context: ParserContext<T>) = context.atEndOfInput

  override fun toString() = stringify()

}

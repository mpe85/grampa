package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.ParserContext
import com.mpe85.grampa.util.stringify

/**
 * A rule implementation that matches an empty input.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class EmptyRule<T> : AbstractRule<T>() {

  override fun match(context: ParserContext<T>) = true

  override fun toString() = stringify()

}

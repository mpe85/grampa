package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that matches the end of the input.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class EndOfInputRule<T> : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = context.atEndOfInput
  
  override fun equals(other: Any?) = kotlinEquals(other, arrayOf())
  override fun toString() = kotlinToString(arrayOf())

}

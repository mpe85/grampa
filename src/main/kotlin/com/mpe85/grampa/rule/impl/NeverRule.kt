package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.RuleContext

/**
 * A rule implementation that never matches.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class NeverRule<T> : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = false
  
  override fun equals(other: Any?) = kotlinEquals(other, arrayOf())
  override fun toString() = kotlinToString(arrayOf())

}

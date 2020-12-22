package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.mpe85.grampa.rule.RuleContext

/**
 * A string rule implementation.
 *
 * @author mpe85
 * @param T The type of the stack elements
 * @param string A string
 * @property ignoreCase Indicates if the case should be ignored
 */
class StringRule<T> @JvmOverloads constructor(string: String, private val ignoreCase: Boolean = false) :
  AbstractRule<T>() {

  private val string: String = if (ignoreCase) toLowerCase(string) else string

  override fun match(context: RuleContext<T>): Boolean {
    if (context.numberOfCharsLeft >= string.length) {
      val nextChars = context.inputBuffer.subSequence(context.currentIndex, context.currentIndex + string.length)
      return string == (if (ignoreCase) toLowerCase(nextChars.toString()) else nextChars.toString())
          && context.advanceIndex(string.length)
    }
    return false
  }

  override fun hashCode() = kotlinHashCode(properties)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(StringRule<*>::string, StringRule<*>::ignoreCase)
  }

}

package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A string rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param string a string
 * @property ignoreCase if true, the case is ignored
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

  override fun hashCode() = hash(super.hashCode(), string, ignoreCase)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as StringRule<*>
      return super.equals(other)
          && string == other.string
          && ignoreCase == other.ignoreCase
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("string", string)
    .add("ignoreCase", ignoreCase)

}

package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.charCount
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A code point predicate rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @property predicate a predicate that is tested by the rule
 */
class CodePointPredicateRule<T>(private val predicate: (Int) -> Boolean) : AbstractRule<T>() {

  /**
   * C'tor. Create a code point predicate rules that exactly matches a specific code point.
   *
   * @param codePoint a code point
   */
  constructor(codePoint: Int) : this({ cp -> cp == codePoint })

  override fun match(context: RuleContext<T>) = !context.isAtEndOfInput
      && predicate(context.currentCodePoint)
      && context.advanceIndex(charCount(context.currentCodePoint))

  override fun hashCode() = hash(super.hashCode(), predicate)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as CodePointPredicateRule<*>
      return (super.equals(other)
          && predicate == other.predicate)
    }
    return false
  }

}

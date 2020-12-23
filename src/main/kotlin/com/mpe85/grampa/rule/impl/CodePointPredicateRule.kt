package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinToString
import com.ibm.icu.lang.UCharacter.charCount
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.checkEquality
import java.util.Objects.hash

/**
 * A code point predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
class CodePointPredicateRule<T>(private val predicate: (Int) -> Boolean) : AbstractRule<T>() {

  /**
   * Construct a code point predicate rule that exactly matches a specific code point.
   *
   * @param[codePoint] A code point
   */
  constructor(codePoint: Int) : this({ cp -> cp == codePoint })

  /**
   * Construct a code point predicate rule that exactly matches a specific character.
   *
   * @param[char] A character
   */
  constructor(char: Char) : this({ cp -> cp == char.toInt() })

  override fun match(context: RuleContext<T>) = !context.atEndOfInput
      && predicate(context.currentCodePoint)
      && context.advanceIndex(charCount(context.currentCodePoint))

  override fun hashCode() = hash(super.hashCode(), predicate)
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, CodePointPredicateRule<T>::predicate)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(CodePointPredicateRule<*>::predicate)
  }

}

package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.RuleContext

/**
 * A character predicate rule implementation.
 *
 * @author mpe85
 * @param T The type of the stack elements
 * @property predicate A predicate that is tested by the rule
 */
class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) : AbstractRule<T>() {

  /**
   * Construct a character predicate rule that exactly matches a specific character.
   *
   * @param character a character
   */
  constructor(character: Char) : this({ c -> c == character })

  override fun match(context: RuleContext<T>) = !context.isAtEndOfInput
      && predicate(context.currentChar)
      && context.advanceIndex(1)

  override fun hashCode() = kotlinHashCode(properties)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(CharPredicateRule<*>::predicate)
  }

}

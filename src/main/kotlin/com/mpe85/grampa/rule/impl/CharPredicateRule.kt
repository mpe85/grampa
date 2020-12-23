package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A character predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) : AbstractRule<T>() {

  /**
   * Construct a character predicate rule that exactly matches a specific character.
   *
   * @param[character] A character
   */
  constructor(character: Char) : this({ c -> c == character })

  override fun match(context: RuleContext<T>) = !context.atEndOfInput
      && predicate(context.currentChar)
      && context.advanceIndex(1)

  override fun hashCode() = hash(super.hashCode(), predicate)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(CharPredicateRule<*>::predicate)
  }

}

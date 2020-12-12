package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash
import java.util.function.Predicate

/**
 * A character predicate rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param predicate a predicate that is tested by the rule.
 */
class CharPredicateRule<T>(private val predicate: Predicate<Char>) : AbstractRule<T>() {

  /**
   * C'tor. Create a character predicate rules that exactly matches a specific character.
   *
   * @param character a character
   */
  constructor(character: Char) : this({ c: Char -> c == character })

  override fun match(context: RuleContext<T>) = !context.isAtEndOfInput
      && predicate.test(context.currentChar)
      && context.advanceIndex(1)

  override fun hashCode() = hash(super.hashCode(), predicate)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as CharPredicateRule<*>
      return (super.equals(other)
          && predicate == other.predicate)
    }
    return false
  }

}

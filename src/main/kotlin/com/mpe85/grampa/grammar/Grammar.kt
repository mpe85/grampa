package com.mpe85.grampa.grammar

import com.mpe85.grampa.rule.Rule

/**
 * The grammar interface.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
interface Grammar<T> {

  /**
   * Get the root rule of the grammer.
   *
   * @return A grammar rule
   */
  fun root(): Rule<T>

}

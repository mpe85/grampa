package com.mpe85.grampa.parser

import com.mpe85.grampa.rule.Rule

/**
 * Defines a parser.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
interface Parser<T> {

  /**
   * Get the root rule of the parser.
   *
   * @return a parser rule
   */
  fun root(): Rule<T>

}

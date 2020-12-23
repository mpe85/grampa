package com.mpe85.grampa.parser

import com.mpe85.grampa.rule.Rule

/**
 * The parser interface.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
interface Parser<T> {

  /**
   * Get the root rule of the parser.
   *
   * @return A parser rule
   */
  fun root(): Rule<T>

}

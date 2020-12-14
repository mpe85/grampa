package com.mpe85.grampa.rule

import com.mpe85.grampa.input.InputBuffer

/**
 * Defines a context for parser rules.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @property inputBuffer the underlying input buffer
 * @property currentIndex the current index inside the parser input
 * @property parent the parent context of the context
 */
interface RuleContext<T> : ActionContext<T> {

  val inputBuffer: InputBuffer
  override var currentIndex: Int
  override val parent: RuleContext<T>?

  /**
   * Advance the current index inside the parser input.
   *
   * @param delta number of characters to advance
   * @return true if the advancing was possible, otherwise false
   */
  fun advanceIndex(delta: Int): Boolean

  /**
   * Run the parser against the input.
   *
   * @return true if the parser successfully matched the input, otherwise false
   */
  fun run(): Boolean

  /**
   * Create a child context out of the context that is used to run child rules.
   *
   * @param rule a child rule for which the child context should be created for
   * @return a rule contexts
   */
  fun createChildContext(rule: Rule<T>): RuleContext<T>

}

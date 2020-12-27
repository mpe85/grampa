package com.mpe85.grampa.rule

/**
 * A parser action.
 *
 * @author mpe85
 * @param[T] the type of the stack elements
 */
@FunctionalInterface
fun interface Action<T> {

  /**
   * Run the action.
   *
   * @param [context] an action context
   * @return true if the action was successfully run
   */
  fun run(context: RuleContext<T>): Boolean

}

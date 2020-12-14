package com.mpe85.grampa.rule

/**
 * A parser action
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
fun interface Action<T> {

  /**
   * Runs the action.
   *
   * @param context an action context
   * @return true if the action was successfully run, false otherwise
   */
  fun run(context: ActionContext<T>): Boolean

}

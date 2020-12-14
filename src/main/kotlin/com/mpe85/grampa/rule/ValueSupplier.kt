package com.mpe85.grampa.rule

/**
 * A supplier for parser stack values.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
fun interface ValueSupplier<T> {

  /**
   * Supply a value for the parser stack.
   *
   * @param context an action context
   * @return a stack value
   */
  fun supply(context: ActionContext<T>): T

}

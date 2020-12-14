package com.mpe85.grampa.rule

/**
 * A supplier for parser events.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
fun interface EventSupplier<T> {

  /**
   * Supply a parser event.
   *
   * @param context an action context
   * @return an event
   */
  fun supply(context: ActionContext<T>): Any

}

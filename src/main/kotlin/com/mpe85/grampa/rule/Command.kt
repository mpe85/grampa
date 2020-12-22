package com.mpe85.grampa.rule

/**
 * A parser command. A command is a special [Action] that always succeeds (i.e. always returns true)
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
@FunctionalInterface
fun interface Command<T> {

  /**
   * Executes the parser command.
   *
   * @param context an action context
   */
  fun execute(context: ActionContext<T>)

}

/**
 * Convert the parser command to a parser action.
 *
 * @return a parser action
 */
fun <T> Command<T>.toAction() = Action<T> { ctx ->
  execute(ctx)
  true
}

package com.mpe85.grampa.rule

/**
 * A parser command. A command is a special [Action] that always succeeds (i.e. always returns true)
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
@FunctionalInterface
fun interface Command<T> {

  /**
   * Execute the parser command.
   *
   * @param[context] An action context
   */
  fun execute(context: RuleContext<T>)

}

/**
 * Convert the parser command to a parser action.
 *
 * @return A parser action
 */
fun <T> Command<T>.toAction() = Action<T> { ctx ->
  execute(ctx)
  true
}

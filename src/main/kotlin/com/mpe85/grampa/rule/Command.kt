package com.mpe85.grampa.rule

import com.mpe85.grampa.context.RuleContext

/**
 * A parser command. A command is a special [Action] that always succeeds (i.e. always returns true).
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
@FunctionalInterface
public fun interface Command<T> {

    /**
     * Execute the parser command.
     *
     * @param[context] A rule context
     */
    public fun execute(context: RuleContext<T>)
}

/**
 * Convert the parser command to a parser action.
 *
 * @return A parser action
 */
public fun <T> Command<T>.toAction(): Action<T> = Action<T> { execute(it); true }

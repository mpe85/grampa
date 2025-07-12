package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.RuleContext

/**
 * A parser command. A command is a special [Action] that always succeeds (i.e. always returns
 * true).
 *
 * @param[T] The type of the stack elements
 * @author mpe85
 */
@FunctionalInterface
public fun interface Command<T> {

    /**
     * Execute the parser command.
     *
     * @param[context] A rule context
     */
    public fun execute(context: RuleContext<T>)

    /**
     * Convert this command to an action.
     *
     * @return The converted action
     */
    public fun toAction(): Action<T> = Action { ctx ->
        execute(ctx)
        true
    }
}

/**
 * Convert this command function to an action function.
 *
 * @return The converted action function
 */
public fun <T> ((RuleContext<T>) -> Unit).toAction(): (RuleContext<T>) -> Boolean = { ctx ->
    invoke(ctx)
    true
}

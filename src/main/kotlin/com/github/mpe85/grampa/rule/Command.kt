package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.RuleContext

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

    /**
     * Convert this command to an action.
     *
     * @return The converted action
     */
    public fun toAction(): Action<T> = Action { execute(it); true }
}

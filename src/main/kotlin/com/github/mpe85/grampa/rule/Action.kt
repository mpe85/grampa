package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.RuleContext

/**
 * A parser action.
 *
 * @param[T] the type of the stack elements
 * @author mpe85
 */
@FunctionalInterface
public fun interface Action<T> {

    /**
     * Run the action.
     *
     * @param [context] an action context
     * @return true if the action was successfully run
     */
    public fun run(context: RuleContext<T>): Boolean
}

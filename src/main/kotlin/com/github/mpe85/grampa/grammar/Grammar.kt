package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.rule.Rule

/**
 * The grammar interface.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
public interface Grammar<T> {

    /**
     * Get the starting rule of the grammar.
     *
     * @return A grammar rule
     */
    public fun start(): Rule<T>
}

package com.mpe85.grampa.grammar

import com.mpe85.grampa.rule.Rule

/**
 * The grammar interface.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
public interface Grammar<T> {

    /**
     * Get the root rule of the grammar.
     *
     * @return A grammar rule
     */
    public fun root(): Rule<T>
}

package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.rule.Rule

/**
 * The grammar interface.
 * Note that a grammar instance is thread-safe,
 * and it is usually sufficient if only a single instance is created and shared between threads.
 * You should be careful if you have any rules with code blocks (like command, action, etc.)
 * that are not thread-safe themselves; in such a case the grammar is not thread-safe either.
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

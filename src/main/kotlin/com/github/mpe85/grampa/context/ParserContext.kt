package com.github.mpe85.grampa.context

import com.github.mpe85.grampa.input.InputBuffer
import com.github.mpe85.grampa.rule.Rule

/**
 * A context for parsers.
 *
 * @param[T] The type of the stack elements
 * @property[inputBuffer] The underlying input buffer
 * @property[currentIndex] The current index inside the parser input
 * @property[parent] The parent context of the context
 * @author mpe85
 */
public interface ParserContext<T> : RuleContext<T> {

    public val inputBuffer: InputBuffer
    override var currentIndex: Int
    override val parent: ParserContext<T>?

    /**
     * Advance the current index inside the parser input.
     *
     * @param[delta] The number of characters to advance
     * @return true if the advancing was possible
     */
    public fun advanceIndex(delta: Int): Boolean

    /**
     * Run the parser against the input.
     *
     * @return true if the parser successfully matched the input
     */
    public fun run(): Boolean

    /**
     * Create a child context out of the context that is used to run child rules.
     *
     * @param[rule] A child rule for which the child context should be created for
     * @return A new parser context
     */
    public fun createChildContext(rule: Rule<T>): ParserContext<T>
}

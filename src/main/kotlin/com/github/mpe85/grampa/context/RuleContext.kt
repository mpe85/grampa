package com.github.mpe85.grampa.context

import com.github.mpe85.grampa.input.InputPosition
import com.github.mpe85.grampa.stack.RestorableStack
import org.greenrobot.eventbus.EventBus

/**
 * Defines a context for parser action rules.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[level] The current level (depth) inside the context parent/child hierarchy
 * @property[startIndex] The start index inside the parser input where the context tries to match its rule
 * @property[currentIndex] The current index inside the parser input
 * @property[atEndOfInput] Whether the current index is at the end of the input
 * @property[currentChar] The character at the current index
 * @property[currentCodePoint] The code point at the current index
 * @property[numberOfCharsLeft] The number of characters left in the input.
 * @property[input] The entire parser input
 * @property[matchedInput] The input that is already matched successfully
 * @property[restOfInput] The rest of the input that is not matched yet
 * @property[previousMatch] The part of the input that was matched by the previous rule
 * @property[position] The position of the current index
 * @property[inTestRule] Whether the context is inside a test rule
 * @property[stack] The parser stack
 * @property[bus] The parser event bus
 * @property[parent] The parent context of the context
 */
public interface RuleContext<T> {
    public val level: Int
    public val startIndex: Int
    public val currentIndex: Int
    public val atEndOfInput: Boolean
    public val currentChar: Char
    public val currentCodePoint: Int
    public val numberOfCharsLeft: Int
    public val input: CharSequence
    public val matchedInput: CharSequence
    public val restOfInput: CharSequence
    public val previousMatch: CharSequence?
    public val position: InputPosition
    public val inTestRule: Boolean
    public val stack: RestorableStack<T>
    public val bus: EventBus
    public val parent: RuleContext<T>?
}

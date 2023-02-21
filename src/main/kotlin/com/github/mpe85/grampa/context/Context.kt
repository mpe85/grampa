package com.github.mpe85.grampa.context

import com.github.mpe85.grampa.event.MatchFailureEvent
import com.github.mpe85.grampa.event.MatchSuccessEvent
import com.github.mpe85.grampa.event.PreMatchEvent
import com.github.mpe85.grampa.input.InputBuffer
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.stack.RestorableStack
import org.greenrobot.eventbus.EventBus

/**
 * An internal context state.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[inputBuffer] The underlying input buffer
 * @property[level] The current context level, corresponds to the number of parents that a context (recursively) has
 * @property[rule] The rule that is currently executed
 * @property[startIndex] The start index at the beginning of the rule execution
 * @property[stack] The parser stack
 * @property[bus] The parser event bus
 * @property[parentContext] The optional parent context
 */
internal data class ContextState<T>(
    val inputBuffer: InputBuffer,
    val level: Int,
    val rule: Rule<T>,
    val startIndex: Int,
    val stack: RestorableStack<T>,
    val bus: EventBus,
    val parentContext: ParserContext<T>? = null,
)

/**
 * The internal context implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[state] The current context state
 * @property[rule] The rule that is currently executed
 * @property[cachedCurrentChar] The cached current character
 * @property[cachedCurrentCodePoint] The cached current code point
 */
internal class Context<T>(state: ContextState<T>) : ParserContext<T> {

    override val inputBuffer = state.inputBuffer
    override val level = state.level
    private val rule = state.rule
    override val startIndex = state.startIndex
    override val stack = state.stack
    override val bus = state.bus
    override val parent = state.parentContext

    override var currentIndex = startIndex
        set(currentIndex) {
            require(currentIndex in 0..inputBuffer.length) { "A 'currentIndex' must not be out of bounds." }
            if (currentIndex >= this.currentIndex) {
                previousMatch = inputBuffer.subSequence(this.currentIndex, currentIndex)
            }
            invalidateCache()
            field = currentIndex
        }

    private var cachedCurrentChar: Char? = null
    private var cachedCurrentCodePoint: Int? = null
    override var previousMatch = parent?.previousMatch

    override val atEndOfInput get() = currentIndex == inputBuffer.length

    override val currentChar: Char
        get() {
            if (cachedCurrentChar == null) {
                cachedCurrentChar = inputBuffer.getChar(currentIndex)
            }
            return cachedCurrentChar as Char
        }

    override val currentCodePoint: Int
        get() {
            if (cachedCurrentCodePoint == null) {
                cachedCurrentCodePoint = inputBuffer.getCodePoint(currentIndex)
            }
            return cachedCurrentCodePoint as Int
        }

    override val numberOfCharsLeft get() = inputBuffer.length - currentIndex

    override val input get() = inputBuffer.subSequence(0, inputBuffer.length)

    override val matchedInput get() = inputBuffer.subSequence(0, currentIndex)

    override val restOfInput get() = inputBuffer.subSequence(currentIndex, inputBuffer.length)

    override val position get() = inputBuffer.getPosition(currentIndex)

    override val inTestRule get() = rule.testRule || parent?.inTestRule ?: false

    override fun advanceIndex(delta: Int): Boolean {
        require(delta >= 0) { "A 'delta' must be greater or equal to 0." }
        if (currentIndex + delta <= inputBuffer.length) {
            currentIndex += delta
            invalidateCache()
            return true
        }
        return false
    }

    override fun run(): Boolean {
        stack.takeSnapshot()
        bus.post(PreMatchEvent(this))
        return rule.match(this).also { matched ->
            if (matched) {
                parent?.currentIndex = currentIndex
                bus.post(MatchSuccessEvent(this))
            } else {
                bus.post(MatchFailureEvent(this))
            }
            stack.removeSnapshot(!matched)
        }
    }

    override fun createChildContext(rule: Rule<T>) =
        Context(ContextState(inputBuffer, level + 1, rule, currentIndex, stack, bus, this))

    /**
     * Invalidate the internal cache.
     */
    private fun invalidateCache() {
        cachedCurrentChar = null
        cachedCurrentCodePoint = null
    }
}

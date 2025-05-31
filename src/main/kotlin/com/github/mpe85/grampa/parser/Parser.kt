package com.github.mpe85.grampa.parser

import com.github.mpe85.grampa.context.Context
import com.github.mpe85.grampa.context.ContextState
import com.github.mpe85.grampa.createGrammar
import com.github.mpe85.grampa.event.ParseEventListener
import com.github.mpe85.grampa.event.PostParseEvent
import com.github.mpe85.grampa.event.PreParseEvent
import com.github.mpe85.grampa.grammar.Grammar
import com.github.mpe85.grampa.grammar.ValidGrammar
import com.github.mpe85.grampa.input.CharSequenceInputBuffer
import com.github.mpe85.grampa.input.InputBuffer
import com.github.mpe85.grampa.stack.LinkedListRestorableStack
import org.greenrobot.eventbus.EventBus

/**
 * The parser that parses an input text using a given parser grammar.
 * An instance of this class can be used to parse several inputs one after the other,
 * but it is not thread-safe since it uses a single internal state that is reset when a new parser run is started.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[grammar] A grammar instance that must be created beforehand by any of the [createGrammar] functions
 * @property[startRule] The start rule of the parser's grammar
 * @property[bus] The parser's event bus
 * @property[stack] The parser's value stack
 */
public class Parser<T>(grammar: Grammar<T>) {

    init {
        require(grammar is ValidGrammar) {
            "The given grammar instance '$grammar' is invalid. Did you instantiate it using createGrammar()?"
        }
    }

    private val startRule = grammar.start()
    private val bus = EventBus.builder().logNoSubscriberMessages(false).build()
    private var stack = LinkedListRestorableStack<T>()

    /**
     * Register a listener to the parser event bus.
     *
     * @param[listener] A parse event listener
     */
    public fun registerListener(listener: ParseEventListener<T>): Unit = bus.register(listener)

    /**
     * Unregister a listener from the parser event bus.
     *
     * @param[listener] A parse event listener
     */
    public fun unregisterListener(listener: ParseEventListener<T>): Unit = bus.unregister(listener)

    /**
     * Run the parser against a character sequence.
     * Can be called several times for different inputs, but not in parallel (not thread-safe).
     *
     * @param[charSequence] A character sequence
     * @return The parse result
     */
    public fun run(charSequence: CharSequence): ParseResult<T> = run(CharSequenceInputBuffer(charSequence))

    /**
     * Run the parser against an input buffer.
     * Can be called several times for different inputs, but not in parallel (not thread-safe).
     *
     * @param[inputBuffer] An input buffer
     * @return The parse result
     */
    public fun run(inputBuffer: InputBuffer): ParseResult<T> = createStartContext(inputBuffer).let { ctx ->
        bus.post(PreParseEvent(ctx))
        ParseResult(ctx.run(), ctx).also { bus.post(PostParseEvent(it)) }
    }

    /**
     * Create the initial start context for the parser's start rule.
     *
     * @param[inputBuffer] An input buffer
     * @return A rule context
     */
    private fun createStartContext(inputBuffer: InputBuffer) = stack.run {
        reset()
        Context(ContextState(inputBuffer, 0, startRule, 0, this, bus))
    }
}

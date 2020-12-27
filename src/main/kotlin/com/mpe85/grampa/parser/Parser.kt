package com.mpe85.grampa.parser

import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.event.PreParseEvent
import com.mpe85.grampa.grammar.Grammar
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.DefaultContext
import com.mpe85.grampa.rule.impl.DefaultContextState
import com.mpe85.grampa.stack.impl.LinkedListRestorableStack
import org.greenrobot.eventbus.EventBus

/**
 * The parser that parses an input text using a given parser grammar.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[grammar] A grammar instance
 * @property[rootRule] The root rule of the parser's grammar
 * @property[bus] The parser's event bus
 * @property[stack] The parser's value stack
 */
class Parser<T>(grammar: Grammar<T>) {

  private val rootRule: Rule<T> = grammar.root()
  private val bus = EventBus.builder().logNoSubscriberMessages(false).build()
  private var stack = LinkedListRestorableStack<T>()

  /**
   * Register a listener to the parser event bus.
   *
   * @param[listener] A parse event listener
   */
  fun registerListener(listener: ParseEventListener<T>) = bus.register(listener)

  /**
   * Unregister a listener from the parser event bus.
   *
   * @param[listener] A parse event listener
   */
  fun unregisterListener(listener: ParseEventListener<T>) = bus.unregister(listener)

  /**
   * Run the parser against a character sequence.
   *
   * @param[charSequence] A character sequence
   * @return The parse result
   */
  fun run(charSequence: CharSequence) = run(CharSequenceInputBuffer(charSequence))

  /**
   * Run the parser against an input buffer. This function must only be called once.
   *
   * @param[inputBuffer] An input buffer
   * @return The parse result
   */
  fun run(inputBuffer: InputBuffer) = createRootContext(inputBuffer).let { ctx ->
    bus.post(PreParseEvent(ctx))
    ParseResult(ctx.run(), ctx).also { res ->
      bus.post(PostParseEvent(res))
    }
  }

  /**
   * Create the initial root context for the parser's root rule.
   *
   * @param[inputBuffer] An input buffer
   * @return A rule context
   */
  private fun createRootContext(inputBuffer: InputBuffer) =
    DefaultContext(DefaultContextState(inputBuffer, 0, rootRule, 0, stack.apply { reset() }, bus))

}

package com.mpe85.grampa.runner

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.SubscriberExceptionHandler
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.event.PreParseEvent
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.DefaultContext
import com.mpe85.grampa.util.stack.RestorableStack
import com.mpe85.grampa.util.stack.impl.LinkedListRestorableStack

/**
 * The default parse runner. May be overridden by a custom implementation.
 *
 * @author mpe85
 *
 * @param T the type of the stack elements
 * @param parser a parser instance
 * @param handler a handler for parser events
 */
open class DefaultParseRunner<T> @JvmOverloads constructor(
  parser: Parser<T>,
  handler: SubscriberExceptionHandler? = null
) {
  /**
   * Gets the root rule of the parser.
   *
   * @return the root rule
   */
  val rootRule = parser.root()
  private val bus = handler?.let { EventBus(it) } ?: EventBus()
  private var valueStack: RestorableStack<T>? = null

  /**
   * Registers a listener to the parser event bus.
   *
   * @param listener a parse event listener
   */
  fun registerListener(listener: ParseEventListener<T>) = bus.register(listener)

  /**
   * Unregisters a listener to the parser event bus.
   *
   * @param listener a parse event listener
   */
  fun unregisterListener(listener: ParseEventListener<T>) = bus.unregister(listener)

  /**
   * Runs the parser against a character sequence.
   *
   * @param charSequence a character sequence
   * @return the parse result
   */
  fun run(charSequence: CharSequence?) = run(CharSequenceInputBuffer(charSequence!!))

  /**
   * Runs the parser against an input buffer.
   *
   * @param inputBuffer an input buffer
   * @return the parse result
   */
  fun run(inputBuffer: InputBuffer): ParseResult<T> {
    resetStack()
    return createRootContext(inputBuffer).let { ctx ->
      bus.post(PreParseEvent(ctx))
      ParseResult(ctx.run(), ctx).also { res ->
        bus.post(PostParseEvent(res))
      }
    }
  }

  /**
   * Creates the initial root context for the parser's root rule.
   *
   * @param inputBuffer an input buffer
   * @return a rule context
   */
  protected fun createRootContext(inputBuffer: InputBuffer?) =
    DefaultContext(inputBuffer, 0, rootRule, 0, valueStack, bus)

  /**
   * Resets (clears) the stack.
   */
  private fun resetStack() {
    valueStack = LinkedListRestorableStack()
  }

}

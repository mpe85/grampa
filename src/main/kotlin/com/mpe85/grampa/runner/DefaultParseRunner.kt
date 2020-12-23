package com.mpe85.grampa.runner

import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.event.PreParseEvent
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.DefaultContext
import com.mpe85.grampa.stack.RestorableStack
import com.mpe85.grampa.stack.impl.LinkedListRestorableStack
import org.greenrobot.eventbus.EventBus

/**
 * The default parse runner. May be overridden by a custom implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[parser] A parser instance
 */
open class DefaultParseRunner<T>(parser: Parser<T>) {

  /**
   * Get the root rule of the parser.
   *
   * @return The root rule
   */
  val rootRule: Rule<T> = parser.root()
  private val bus = EventBus.builder().logNoSubscriberMessages(false).build()
  private var valueStack: RestorableStack<T>? = null

  /**
   * Register a listener to the parser event bus.
   *
   * @param[listener] A parse event listener
   */
  fun registerListener(listener: ParseEventListener<T>) = bus.register(listener)

  /**
   * Unregister a listener to the parser event bus.
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
   * Run the parser against an input buffer.
   *
   * @param[inputBuffer] An input buffer
   * @return The parse result
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
   * Create the initial root context for the parser's root rule.
   *
   * @param[inputBuffer] An input buffer
   * @return A rule context
   */
  protected fun createRootContext(inputBuffer: InputBuffer) =
    DefaultContext(inputBuffer, 0, rootRule, 0, valueStack!!, bus)

  /**
   * Reset (clear) the stack.
   */
  private fun resetStack() {
    valueStack = LinkedListRestorableStack()
  }

}

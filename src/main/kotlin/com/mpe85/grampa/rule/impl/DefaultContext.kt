package com.mpe85.grampa.rule.impl

import com.google.common.base.Preconditions
import com.mpe85.grampa.event.MatchFailureEvent
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.PreMatchEvent
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.stack.RestorableStack
import org.greenrobot.eventbus.EventBus

class DefaultContext<T> @JvmOverloads constructor(
  override val inputBuffer: InputBuffer,
  override val level: Int,
  private val rule: Rule<T>,
  override val startIndex: Int,
  override val stack: RestorableStack<T>,
  private val bus: EventBus,
  val parentContext: RuleContext<T>? = null
) : RuleContext<T>, ActionContext<T> {

  override var currentIndex = startIndex
    set(currentIndex) {
      Preconditions.checkPositionIndex(currentIndex, inputBuffer.length, "A 'currentIndex' must be in range.")
      if (currentIndex > this.currentIndex) {
        previousMatch = inputBuffer.subSequence(this.currentIndex, currentIndex)
      }
      invalidateCache()
      field = currentIndex
    }

  private var cachedCurrentChar: Char? = null
  private var cachedCurrentCodePoint: Int? = null
  override var previousMatch = parentContext?.previousMatch


  override val isAtEndOfInput: Boolean
    get() = currentIndex == inputBuffer.length

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

  override val numberOfCharsLeft: Int
    get() = inputBuffer.length - currentIndex

  override val input: CharSequence
    get() = inputBuffer.subSequence(0, inputBuffer.length)

  override val matchedInput: CharSequence
    get() = inputBuffer.subSequence(0, currentIndex)

  override val restOfInput: CharSequence
    get() = inputBuffer.subSequence(currentIndex, inputBuffer.length)

  override val position: InputPosition
    get() = inputBuffer.getPosition(currentIndex)

  override val inPredicate: Boolean
    get() = rule.isPredicate || parentContext?.inPredicate ?: false

  override val parent: RuleContext<T>?
    get() = parentContext

  override fun post(event: Any) = bus.post(event)

  override fun advanceIndex(delta: Int): Boolean {
    Preconditions.checkArgument(delta >= 0, "A 'delta' must be greater or equal 0.")
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
    val matched = rule.match(this)
    if (matched && parentContext != null) {
      parentContext.currentIndex = currentIndex
    }
    if (matched) {
      bus.post(MatchSuccessEvent(this))
    } else {
      bus.post(MatchFailureEvent(this))
    }
    stack.removeSnapshot(!matched)
    return matched
  }

  override fun createChildContext(rule: Rule<T>) =
    DefaultContext(inputBuffer, level + 1, rule, currentIndex, stack, bus, this)

  private fun invalidateCache() {
    cachedCurrentChar = null
    cachedCurrentCodePoint = null
  }

}

package com.mpe85.grampa.rule.impl

import com.google.common.base.Preconditions
import com.google.common.eventbus.EventBus
import com.mpe85.grampa.event.MatchFailureEvent
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.PreMatchEvent
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.stack.RestorableStack

class DefaultContext<T> @JvmOverloads constructor(
  private val inputBuffer: InputBuffer,
  private val level: Int,
  private val rule: Rule<T>,
  private val startIndex: Int,
  private val stack: RestorableStack<T>,
  private val bus: EventBus,
  private val parentContext: RuleContext<T>? = null
) : RuleContext<T>, ActionContext<T> {

  private var currentIndex = startIndex
  private var cachedCurrentChar: Char? = null
  private var cachedCurrentCodePoint: Int? = null
  private var previousMatch = parentContext?.previousMatch

  override fun getLevel() = level

  override fun getStartIndex() = startIndex

  override fun getCurrentIndex() = currentIndex

  override fun isAtEndOfInput() = currentIndex == inputBuffer.length

  override fun getCurrentChar(): Char {
    if (cachedCurrentChar == null) {
      cachedCurrentChar = inputBuffer.getChar(currentIndex)
    }
    return cachedCurrentChar as Char
  }

  override fun getCurrentCodePoint(): Int {
    if (cachedCurrentCodePoint == null) {
      cachedCurrentCodePoint = inputBuffer.getCodePoint(currentIndex)
    }
    return cachedCurrentCodePoint as Int
  }

  override fun getNumberOfCharsLeft() = inputBuffer.length - currentIndex

  override fun getInput() = inputBuffer.subSequence(0, inputBuffer.length)

  override fun getMatchedInput() = inputBuffer.subSequence(0, currentIndex)

  override fun getRestOfInput() = inputBuffer.subSequence(currentIndex, inputBuffer.length)

  override fun getPreviousMatch() = previousMatch

  override fun getPosition() = inputBuffer.getPosition(currentIndex)

  override fun inPredicate() = rule.isPredicate || parentContext?.inPredicate() ?: false

  override fun getStack() = stack

  override fun getParent() = parentContext

  override fun post(event: Any) = bus.post(event)

  override fun getInputBuffer() = inputBuffer

  override fun setCurrentIndex(currentIndex: Int) {
    Preconditions.checkPositionIndex(currentIndex, inputBuffer.length, "A 'currentIndex' must be in range.")
    if (currentIndex > this.currentIndex) {
      previousMatch = inputBuffer.subSequence(this.currentIndex, currentIndex)
    }
    invalidateCache()
    this.currentIndex = currentIndex
  }

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

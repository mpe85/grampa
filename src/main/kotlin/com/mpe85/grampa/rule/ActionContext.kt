package com.mpe85.grampa.rule

import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.util.stack.RestorableStack

/**
 * Defines a context for parser action rules.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @property level the current level (depth) inside the context parent/child hierarchy
 * @property startIndex the start index inside the parser input where the context tries to match its rule
 * @property currentIndex the current index inside the parser input
 * @property isAtEndOfInput if the current index is at the end of the input
 * @property currentChar the character at the current index
 * @property currentCodePoint the code point at the current index
 * @property numberOfCharsLeft the number of characters left in the input.
 * @property input the entire parser input
 * @property matchedInput the input that is already matched successfully
 * @property restOfInput the rest of the input that is not matched yet
 * @property previousMatch the part of the input that was matched by the previous rule
 * @property position the position of the current index
 * @property inPredicate if the context is inside a predicate rule
 * @property stack the parser stack
 * @property parent the parent context of the context
 */
interface ActionContext<T> {

  val level: Int
  val startIndex: Int
  val currentIndex: Int
  val isAtEndOfInput: Boolean
  val currentChar: Char
  val currentCodePoint: Int
  val numberOfCharsLeft: Int
  val input: CharSequence
  val matchedInput: CharSequence
  val restOfInput: CharSequence
  val previousMatch: CharSequence?
  val position: InputPosition
  val inPredicate: Boolean
  val stack: RestorableStack<T>
  val parent: ActionContext<T>?

  /**
   * Post a parser event to the event bus.
   *
   * @param event an event to post
   */
  fun post(event: Any)

}

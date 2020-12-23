package com.mpe85.grampa.rule

import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.util.stack.RestorableStack

/**
 * Defines a context for parser action rules.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[level] The current level (depth) inside the context parent/child hierarchy
 * @property[startIndex] The start index inside the parser input where the context tries to match its rule
 * @property[currentIndex] The current index inside the parser input
 * @property[atEndOfInput] Indicates if the current index is at the end of the input
 * @property[currentChar] The character at the current index
 * @property[currentCodePoint] The code point at the current index
 * @property[numberOfCharsLeft] The number of characters left in the input.
 * @property[input] The entire parser input
 * @property[matchedInput] The input that is already matched successfully
 * @property[restOfInput] The rest of the input that is not matched yet
 * @property[previousMatch] The part of the input that was matched by the previous rule
 * @property[position] The position of the current index
 * @property[inTestRule] Indicates if the context is inside a test rule
 * @property[stack] The parser stack
 * @property[parent] The parent context of the context
 */
interface ActionContext<T> {

  val level: Int
  val startIndex: Int
  val currentIndex: Int
  val atEndOfInput: Boolean
  val currentChar: Char
  val currentCodePoint: Int
  val numberOfCharsLeft: Int
  val input: CharSequence
  val matchedInput: CharSequence
  val restOfInput: CharSequence
  val previousMatch: CharSequence?
  val position: InputPosition
  val inTestRule: Boolean
  val stack: RestorableStack<T>
  val parent: ActionContext<T>?

  /**
   * Post a parser event to the event bus.
   *
   * @param[event] An event to post
   */
  fun post(event: Any)

}

package com.mpe85.grampa.input

/**
 * An input buffer that buffers the parser input.
 *
 * @author mpe85
 * @property[length] The length of the input
 */
interface InputBuffer {

  val length: Int

  /**
   * Get the character at a given index.
   *
   * @param [index] A valid index inside the input
   * @return The character at the given index
   */
  fun getChar(index: Int): Char

  /**
   * Get the code point at a given index.
   *
   * @param[index] A valid index inside the input
   * @return A code point at the given index
   */
  fun getCodePoint(index: Int): Int

  /**
   * Get a sub sequence of the input.
   *
   * @param [startIndex] a valid start index
   * @param [endIndex] a valid end index
   * @return the sub sequence
   */
  fun subSequence(startIndex: Int, endIndex: Int): CharSequence

  /**
   * Get the position (line and column) of a character at a given index inside the input.
   *
   * @param[index] A valid index
   * @return The position at the given index
   */
  fun getPosition(index: Int): InputPosition

}

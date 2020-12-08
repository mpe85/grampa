package com.mpe85.grampa.input

/**
 * An input buffer that buffers the parser input.
 *
 * @author mpe85
 */
interface InputBuffer {

  /**
   * Get the character at a given index.
   *
   * @param index a valid index inside the input
   * @return a character
   */
  fun getChar(index: Int): Char

  /**
   * Get the code point at a given index.
   *
   * @param index a valid index inside the input
   * @return a code point
   */
  fun getCodePoint(index: Int): Int

  /**
   * Get the length of the input.
   *
   * @return the length
   */
  val length: Int

  /**
   * Get a sub sequence of the input.
   *
   * @param startIndex a valid start index
   * @param endIndex a valid end index
   * @return the sub sequence
   */
  fun subSequence(startIndex: Int, endIndex: Int): CharSequence

  /**
   * Get the position (line and column) of a character at a given index inside the input.
   *
   * @param index a valid index
   * @return a position
   */
  fun getPosition(index: Int): InputPosition

}

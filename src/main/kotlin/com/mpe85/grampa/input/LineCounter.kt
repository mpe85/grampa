package com.mpe85.grampa.input

/**
 * A counter for lines in texts.
 *
 * @author mpe85
 */
interface LineCounter {

  /**
   * Get the line count.
   *
   * @return the line count
   */
  val lineCount: Int

  /**
   * Get the position of a character at a given index.
   *
   * @param index a valid index
   * @return the input position
   */
  fun getPosition(index: Int): InputPosition?

}
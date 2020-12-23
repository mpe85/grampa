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
   * @return The line count
   */
  val lineCount: Int

  /**
   * Get the position of a character at a given index.
   *
   * @param[index] A valid index
   * @return The input position
   */
  fun getPosition(index: Int): InputPosition

}

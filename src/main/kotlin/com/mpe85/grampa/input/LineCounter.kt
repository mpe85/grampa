package com.mpe85.grampa.input

/**
 * A counter for lines in texts.
 *
 * @author mpe85
 * @property[lineCount] The line count
 */
interface LineCounter {

    val lineCount: Int

    /**
     * Get the position of a character at a given index.
     *
     * @param[index] A valid index
     * @return The input position at the given index
     */
    fun getPosition(index: Int): InputPosition

}

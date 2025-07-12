package com.github.mpe85.grampa.input

/**
 * A counter for lines in texts.
 *
 * @property[lineCount] The line count
 * @author mpe85
 */
public interface LineCounter {

    public val lineCount: Int

    /**
     * Get the position of a character at a given index.
     *
     * @param[index] A valid index
     * @return The input position at the given index
     */
    public fun getPosition(index: Int): InputPosition
}

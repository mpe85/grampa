package com.github.mpe85.grampa.input

/**
 * An input buffer that buffers the parser input.
 *
 * @property[length] The length of the input
 * @author mpe85
 */
public interface InputBuffer {

    public val length: Int

    /**
     * Get the character at a given index.
     *
     * @param [index] A valid index inside the input
     * @return The character at the given index
     */
    public fun getChar(index: Int): Char

    /**
     * Get the code point at a given index.
     *
     * @param[index] A valid index inside the input
     * @return A code point at the given index
     */
    public fun getCodePoint(index: Int): Int

    /**
     * Get a subsequence of the input.
     *
     * @param [start] a valid start index
     * @param [end] a valid end index
     * @return the subsequence
     */
    public fun subSequence(start: Int, end: Int): CharSequence

    /**
     * Get the position (line and column) of a character at a given index inside the input.
     *
     * @param[index] A valid index
     * @return The position at the given index
     */
    public fun getPosition(index: Int): InputPosition
}

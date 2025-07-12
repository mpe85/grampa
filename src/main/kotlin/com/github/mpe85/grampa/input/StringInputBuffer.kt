package com.github.mpe85.grampa.input

/**
 * An [InputBuffer] implementation using a [String].
 *
 * @property[string] A string backing the input buffer
 * @author mpe85
 */
public class StringInputBuffer(private val string: String) : CharSequenceInputBuffer(string) {

    override fun getCodePoint(index: Int): Int = string.codePointAt(index)
}

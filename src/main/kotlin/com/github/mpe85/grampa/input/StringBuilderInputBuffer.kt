package com.github.mpe85.grampa.input

/**
 * An [InputBuffer] implementation using a [StringBuilder].
 *
 * @author mpe85
 * @param[stringBuilder] A string builder backing the input buffer
 */
public class StringBuilderInputBuffer(private val stringBuilder: StringBuilder) :
    CharSequenceInputBuffer(stringBuilder) {

    override fun getCodePoint(index: Int): Int = stringBuilder.codePointAt(index)
}

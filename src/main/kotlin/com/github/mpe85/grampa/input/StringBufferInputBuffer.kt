package com.github.mpe85.grampa.input

/**
 * An [InputBuffer] implementation using a [StringBuffer].
 *
 * @property[stringBuffer] A string buffer backing the input buffer
 * @author mpe85
 */
public class StringBufferInputBuffer(private val stringBuffer: StringBuffer) :
    CharSequenceInputBuffer(stringBuffer) {

    override fun getCodePoint(index: Int): Int = stringBuffer.codePointAt(index)
}

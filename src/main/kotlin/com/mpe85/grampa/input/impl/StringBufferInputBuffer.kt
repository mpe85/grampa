package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringBuffer].
 *
 * @author mpe85
 * @property[stringBuffer] A string buffer backing the input buffer
 */
public class StringBufferInputBuffer(private val stringBuffer: StringBuffer) : CharSequenceInputBuffer(stringBuffer) {

    override fun getCodePoint(index: Int): Int = stringBuffer.codePointAt(index)
}

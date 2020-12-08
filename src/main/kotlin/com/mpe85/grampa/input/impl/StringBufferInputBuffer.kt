package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringBuffer].
 *
 * @author mpe85
 * @param stringBuffer a string buffer backing the input buffer
 */
class StringBufferInputBuffer(private val stringBuffer: StringBuffer) : CharSequenceInputBuffer(stringBuffer) {

  override fun getCodePoint(index: Int) = stringBuffer.codePointAt(index)

}

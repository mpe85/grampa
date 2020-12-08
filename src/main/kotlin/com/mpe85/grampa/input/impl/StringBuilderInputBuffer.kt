package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringBuilder].
 *
 * @author mpe85
 */
class StringBuilderInputBuffer(private val stringBuilder: StringBuilder) : CharSequenceInputBuffer(stringBuilder) {

  override fun getCodePoint(index: Int) = stringBuilder.codePointAt(index)

}

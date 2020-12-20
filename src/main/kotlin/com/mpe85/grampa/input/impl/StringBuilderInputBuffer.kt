package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringBuilder].
 *
 * @author mpe85
 * @param stringBuilder a string builder backing the input buffer
 */
class StringBuilderInputBuffer(private val stringBuilder: StringBuilder) : CharSequenceInputBuffer(stringBuilder) {

  override fun getCodePoint(index: Int) = index.let {
    require(it in 0 until length) { "An 'index' must not be out of bounds." }
    stringBuilder.codePointAt(it)
  }

}

package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [String].
 *
 * @author mpe85
 * @property[string] A string backing the input buffer
 */
class StringInputBuffer(private val string: String) : CharSequenceInputBuffer(string) {

  override fun getCodePoint(index: Int) = string.codePointAt(index)

}

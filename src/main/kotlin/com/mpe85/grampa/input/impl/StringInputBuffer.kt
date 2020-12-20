package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [String].
 *
 * @author mpe85
 * @param string a string backing the input buffer
 */
class StringInputBuffer(private val string: String) : CharSequenceInputBuffer(string) {

  override fun getCodePoint(index: Int) = index.let {
    require(it in 0 until length) { "An 'index' must not be out of bounds." }
    string.codePointAt(it)
  }


}

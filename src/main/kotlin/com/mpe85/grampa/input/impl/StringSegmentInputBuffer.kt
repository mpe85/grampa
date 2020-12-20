package com.mpe85.grampa.input.impl

import com.ibm.icu.impl.StringSegment
import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringSegment].
 *
 * @author mpe85
 * @param stringSegment a string segment backing the input buffer
 */
class StringSegmentInputBuffer(private val stringSegment: StringSegment) : CharSequenceInputBuffer(stringSegment) {

  override fun getCodePoint(index: Int) = index.let {
    require(it in 0 until length) { "An 'index' must not be out of bounds." }
    stringSegment.codePointAt(it)
  }

}

package com.mpe85.grampa.input.impl

import com.ibm.icu.impl.StringSegment
import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringSegment].
 *
 * @author mpe85
 */
class StringSegmentInputBuffer(private val stringSegment: StringSegment) : CharSequenceInputBuffer(stringSegment) {

  override fun getCodePoint(index: Int) = stringSegment.codePointAt(index)

}

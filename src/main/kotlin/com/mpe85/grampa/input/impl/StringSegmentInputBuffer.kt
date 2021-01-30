package com.mpe85.grampa.input.impl

import com.ibm.icu.impl.StringSegment
import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [StringSegment].
 *
 * @author mpe85
 * @property[stringSegment] A string segment backing the input buffer
 */
public class StringSegmentInputBuffer(private val stringSegment: StringSegment) :
    CharSequenceInputBuffer(stringSegment) {

    override fun getCodePoint(index: Int): Int = stringSegment.codePointAt(index)
}

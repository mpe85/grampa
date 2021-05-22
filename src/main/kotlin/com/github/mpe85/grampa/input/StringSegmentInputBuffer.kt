package com.github.mpe85.grampa.input

import com.ibm.icu.impl.StringSegment

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

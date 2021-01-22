package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [CharSequence].
 *
 * @author mpe85
 * @property[charSequence] A character sequence backing the input buffer
 */
open class CharSequenceInputBuffer(private val charSequence: CharSequence) : InputBuffer {

    override val length get() = charSequence.length
    private val lineCounter = CharSequenceLineCounter(charSequence)

    override fun getChar(index: Int) = charSequence[index]
    override fun getCodePoint(index: Int) = charSequence.toString().codePointAt(index)
    override fun subSequence(startIndex: Int, endIndex: Int) = charSequence.subSequence(startIndex, endIndex)
    override fun getPosition(index: Int) = lineCounter.getPosition(index)
}

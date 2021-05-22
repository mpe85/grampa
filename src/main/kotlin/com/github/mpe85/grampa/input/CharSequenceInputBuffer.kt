package com.github.mpe85.grampa.input

/**
 * An [InputBuffer] implementation using a [CharSequence].
 *
 * @author mpe85
 * @property[charSequence] A character sequence backing the input buffer
 */
public open class CharSequenceInputBuffer(private val charSequence: CharSequence) : InputBuffer {

    override val length: Int get() = charSequence.length
    private val lineCounter = CharSequenceLineCounter(charSequence)

    override fun getChar(index: Int): Char = charSequence[index]
    override fun getCodePoint(index: Int): Int = charSequence.toString().codePointAt(index)
    override fun subSequence(start: Int, end: Int): CharSequence = charSequence.subSequence(start, end)
    override fun getPosition(index: Int): InputPosition = lineCounter.getPosition(index)
}

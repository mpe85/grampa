package com.mpe85.grampa.input.impl

import com.google.common.base.Preconditions
import com.mpe85.grampa.input.InputBuffer

/**
 * An [InputBuffer] implementation using a [CharSequence].
 *
 * @author mpe85
 * @param charSequence a character sequence backing the input buffer
 */
open class CharSequenceInputBuffer(private val charSequence: CharSequence) : InputBuffer {

  private val lineCounter = CharSequenceLineCounter(charSequence)

  override fun getChar(index: Int) =
    charSequence[Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range.")]

  override fun getCodePoint(index: Int) = charSequence.toString()
    .codePointAt(Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range."))

  override val length get() = charSequence.length

  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
    Preconditions.checkPositionIndex(startIndex, length, "A 'startIndex' must not be out of range.")
    Preconditions.checkPositionIndex(endIndex, length, "An 'endIndex' must not be out of range.")
    Preconditions.checkPositionIndexes(startIndex, endIndex, length)
    return charSequence.subSequence(startIndex, endIndex)
  }

  override fun getPosition(index: Int) = lineCounter.getPosition(
    Preconditions.checkElementIndex(index, length, "A 'startIndex' must not be out of range.")
  )

}

package com.mpe85.grampa.input.impl

import com.google.common.base.Preconditions
import com.mpe85.grampa.input.InputBuffer
import com.mpe85.grampa.input.InputPosition

/**
 * An [InputBuffer] implementation using a [CharSequence].
 *
 * @author mpe85
 */
open class CharSequenceInputBuffer(private val charSequence: CharSequence) : InputBuffer {

  private val lineCounter: CharSequenceLineCounter = CharSequenceLineCounter(charSequence)

  override fun getChar(index: Int): Char {
    return charSequence[Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range.")]
  }

  override fun getCodePoint(index: Int): Int {
    return charSequence.toString()
      .codePointAt(Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range."))
  }

  override val length: Int
    get() = charSequence.length

  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence? {
    Preconditions.checkPositionIndex(startIndex, length, "A 'startIndex' must not be out of range.")
    Preconditions.checkPositionIndex(endIndex, length, "An 'endIndex' must not be out of range.")
    Preconditions.checkPositionIndexes(startIndex, endIndex, length)
    return charSequence.subSequence(startIndex, endIndex)
  }

  override fun getPosition(index: Int): InputPosition {
    return lineCounter.getPosition(
      Preconditions.checkElementIndex(index, length, "A 'startIndex' must not be out of range.")
    )
  }

}

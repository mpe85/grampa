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

  override fun getChar(index: Int) = index.let {
    require(it in 0 until length) { "An 'index' must not be out of bounds." }
    charSequence[it]
  }

  override fun getCodePoint(index: Int) = index.let {
    require(it in 0 until length) { "An 'index' must not be out of bounds." }
    charSequence.toString().codePointAt(it)
  }

  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
    require(startIndex in 0..length) { "A 'startIndex' must not be out of bounds." }
    require(endIndex in startIndex..length) { "An 'endIndex' must not be out of bounds." }
    return charSequence.subSequence(startIndex, endIndex)
  }

  override fun getPosition(index: Int) = index.let {
    require(it in 0 until length) { "A 'startIndex' must not be out of bounds." }
    lineCounter.getPosition(it)
  }

}

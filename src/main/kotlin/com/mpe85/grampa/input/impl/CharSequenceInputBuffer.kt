package com.mpe85.grampa.input.impl

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
    charSequence[index.also { require(it in 0 until length) { "An 'index' must not be out of bounds." } }]

  override fun getCodePoint(index: Int) = charSequence.toString()
    .codePointAt(index.also { require(it in 0 until length) { "An 'index' must not be out of bounds." } })

  override val length get() = charSequence.length

  override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
    require(startIndex in 0..length) { "A 'startIndex' must not be out of bounds." }
    require(endIndex in startIndex..length) { "An 'endIndex' must not be out of bounds." }
    return charSequence.subSequence(startIndex, endIndex)
  }

  override fun getPosition(index: Int) = lineCounter.getPosition(
    index.also { require(it in 0 until length) { "A 'startIndex' must not be out of bounds." } }
  )

}

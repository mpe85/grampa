package com.mpe85.grampa.input.impl

import com.google.common.base.Preconditions
import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.input.LineCounter
import java.util.NavigableMap
import java.util.Optional
import java.util.TreeMap

/**
 * A [LineCounter] implementation for [CharSequence]s.
 *
 * @author mpe85
 */
class CharSequenceLineCounter(input: CharSequence) : LineCounter {

  private val lines: NavigableMap<Int, Int> = TreeMap()
  private val length: Int = input.length

  companion object {
    private const val LF = '\n'
  }

  init {
    initLines(input)
  }

  private fun initLines(input: CharSequence) {
    var currentIdx = 0
    var lineStartIdx = 0
    var lineNumber = 0
    while (currentIdx < length) {
      if (input[currentIdx] == LF) {
        lines[lineStartIdx] = ++lineNumber
        lineStartIdx = currentIdx + 1
      } else if (currentIdx == length - 1) {
        lines[lineStartIdx] = ++lineNumber
      }
      currentIdx++
    }
  }

  override val lineCount
    get() = lines.size

  override fun getPosition(index: Int): InputPosition {
    Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range.")
    return Optional.of(index)
      .map { k: Int -> lines.floorEntry(k) }
      .map { e: Map.Entry<Int, Int> -> InputPosition(e.value, index - e.key + 1) }
      .get()
  }

}

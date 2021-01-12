package com.mpe85.grampa.input.impl

import com.mpe85.grampa.input.InputPosition
import com.mpe85.grampa.input.LineCounter
import java.util.*
import kotlin.streams.asSequence

/**
 * A [LineCounter] implementation for [CharSequence]s.
 *
 * @author mpe85
 * @property[input] The input in which the lines are counted
 */
class CharSequenceLineCounter(private val input: CharSequence) : LineCounter {

    override val lineCount get() = lines.size
    private val lines = getLines(input)


    private fun getLines(input: CharSequence): NavigableMap<Int, Int> {
        val map = TreeMap<Int, Int>()
        var lineStartIdx = 0
        var lineNumber = 0
        input.chars().asSequence().forEachIndexed { index, ch ->
            if (ch == LF) {
                map[lineStartIdx] = ++lineNumber
                lineStartIdx = index + 1
            } else if (index == input.length - 1) {
                map[lineStartIdx] = ++lineNumber
            }
        }
        return map
    }

    override fun getPosition(index: Int) = lines.floorEntry(checkBounds(index)).let {
        InputPosition(it.value, index - it.key + 1)
    }

    private fun checkBounds(index: Int) = index.also { input[it] }

    companion object {
        private const val LF: Int = '\n'.toInt()
    }

}

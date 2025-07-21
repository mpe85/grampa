package com.github.mpe85.grampa.input

import java.util.NavigableMap
import java.util.TreeMap

/**
 * A [LineCounter] implementation for [CharSequence]s.
 *
 * @property[input] The input in which the lines are counted
 * @author mpe85
 */
public class CharSequenceLineCounter(private val input: CharSequence) : LineCounter {

    override val lineCount: Int
        get() = lines.size

    private val lines = getLines(input)

    private fun getLines(input: CharSequence): NavigableMap<Int, Int> =
        REGEX.findAll(input)
            .mapIndexed { index, match -> match.range.first to (index + 1) }
            .toMap(TreeMap())

    override fun getPosition(index: Int): InputPosition =
        if (index == input.length) {
            InputPosition.EOI
        } else {
            lines.floorEntry(checkBounds(index)).let { InputPosition(it.value, index - it.key + 1) }
        }

    private fun checkBounds(index: Int) = index.also { input[it] }

    private companion object {
        private val REGEX = Regex(".*?(?:\r\n|\r|\n)|.+$")
    }
}

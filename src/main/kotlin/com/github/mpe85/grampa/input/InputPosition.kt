package com.github.mpe85.grampa.input

import com.github.mpe85.grampa.input.InputPosition.Companion.EOI
import com.github.mpe85.grampa.util.stringify

/**
 * A position inside a text input (line and column).
 *
 * @property[line] The line number
 * @property[column] The column number
 * @author mpe85
 */
public data class InputPosition(val line: Int, val column: Int) {

    /**
     * Checks if this [InputPosition] represents the EOI (end of input).
     *
     * @return true if this position is [EOI], false otherwise.
     */
    public fun isEoi(): Boolean = this == EOI

    override fun toString(): String =
        if (isEoi()) "EOI" else stringify("line" to line, "column" to column)

    public companion object {
        /**
         * Represents the EOI (end of input) position. This special [InputPosition] is used to
         * indicate a position beyond the last character of the input. It's characterized by invalid
         * line and column numbers (e.g., -1 for both) to distinguish it from any valid character
         * position.
         */
        public val EOI: InputPosition = InputPosition(-1, -1)
    }
}

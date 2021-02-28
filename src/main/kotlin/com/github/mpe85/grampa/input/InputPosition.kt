package com.github.mpe85.grampa.input

/**
 * A position inside a text input (line and column).
 *
 * @author mpe85
 *
 * @property[line] The line number
 * @property[column] The column number
 */
public data class InputPosition(val line: Int, val column: Int)

package com.github.mpe85.grampa.event

import com.github.mpe85.grampa.parser.ParseResult

/**
 * Event posted after a parse run.
 *
 * @param[T] The type of the stack elements
 * @property[result] The parse result
 * @author mpe85
 */
public class PostParseEvent<T>(public val result: ParseResult<T>)

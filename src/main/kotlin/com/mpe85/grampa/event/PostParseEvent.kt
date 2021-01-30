package com.mpe85.grampa.event

import com.mpe85.grampa.parser.ParseResult

/**
 * Event posted after a parse run.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[result] The parse result
 */
public class PostParseEvent<T>(public val result: ParseResult<T>)

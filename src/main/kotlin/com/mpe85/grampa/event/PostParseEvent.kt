package com.mpe85.grampa.event

import com.mpe85.grampa.runner.ParseResult

/**
 * Event posted after a parse run.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[result] The parse result
 */
class PostParseEvent<T>(val result: ParseResult<T>)

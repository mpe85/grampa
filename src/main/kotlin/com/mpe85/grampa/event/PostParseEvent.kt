package com.mpe85.grampa.event

import com.mpe85.grampa.runner.ParseResult

/**
 * Event posted after a parse run.
 *
 * @author mpe85
 *
 * @param T the type of the stack elements
 */
class PostParseEvent<T>(val result: ParseResult<T>)

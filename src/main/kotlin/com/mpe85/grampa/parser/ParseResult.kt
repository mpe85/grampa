package com.mpe85.grampa.parser

import com.mpe85.grampa.context.ParserContext

/**
 * A holder for the result of a parse run.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The root context after the parse run
 * @property[matched] true if the parse run matched the input successfully; also true if there was a partial match.
 * @property[matchedEntireInput] true if the parse run matched the entire input successfully
 * @property[matchedInput] The part of the input that matched successfully
 * @property[restOfInput] The rest of the input that did not match successfully
 * @property[stack] The parser's stack
 * @property[stackTop] The top element of the parser's stack, or null if the stack is empty
 */
class ParseResult<T>(val matched: Boolean, context: ParserContext<T>) {

    val matchedEntireInput = matched && context.atEndOfInput
    val matchedInput = if (matched) context.matchedInput else null
    val restOfInput: CharSequence = if (matched) context.restOfInput else context.input
    val stack = context.stack.copy()
    val stackTop get() = if (stack.size > 0) stack.peek() else null
}

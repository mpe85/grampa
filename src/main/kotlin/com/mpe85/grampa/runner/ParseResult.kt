package com.mpe85.grampa.runner

import com.mpe85.grampa.rule.RuleContext

/**
 * Holds the result of a parse run.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param context the root context after the parse run
 * @property matched true if the parse run matched the input successfully; also true if there was a partial match.
 * @property matchedEntireInput true if the parse run matched the entire input successfully
 * @property matchedInput the part of the input that matched successfully
 * @property restOfInput the rest of the input that did not match successfully
 * @property stack the parser's stack
 * @property stackTop the top element of the parser's stack, or null if the stack is empty
 */
class ParseResult<T>(val matched: Boolean, context: RuleContext<T>) {

  val matchedEntireInput = matched && context.isAtEndOfInput
  val matchedInput = if (matched) context.matchedInput else null
  val restOfInput: CharSequence = if (matched) context.restOfInput else context.input
  val stack = context.stack.copy()
  val stackTop get() = if (stack.size > 0) stack.peek() else null

}

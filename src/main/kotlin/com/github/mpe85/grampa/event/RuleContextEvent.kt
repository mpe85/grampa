package com.github.mpe85.grampa.event

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.context.RuleContext

/**
 * Abstract event posted in the context of a rule.
 *
 * @param[T] The type of the stack elements
 * @property[context] The rule context in which the event was posted
 * @author mpe85
 */
public sealed class RuleContextEvent<T>(public val context: RuleContext<T>)

/**
 * Event posted before a parse run.
 *
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 * @author mpe85
 */
public class PreParseEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted before a rule match.
 *
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 * @author mpe85
 */
public class PreMatchEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted on a match success.
 *
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 * @author mpe85
 */
public class MatchSuccessEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted on a match failure.
 *
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 * @author mpe85
 */
public class MatchFailureEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

package com.github.mpe85.grampa.event

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.context.RuleContext

/**
 * Abstract event posted in the context of a rule.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[context] The rule context in which the event was posted
 */
public sealed class RuleContextEvent<T>(public val context: RuleContext<T>)

/**
 * Event posted before a parse run.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 */
public class PreParseEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted before a rule match.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 */
public class PreMatchEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted on a match success.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 */
public class MatchSuccessEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

/**
 * Event posted on a match failure.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 */
public class MatchFailureEvent<T>(context: ParserContext<T>) : RuleContextEvent<T>(context)

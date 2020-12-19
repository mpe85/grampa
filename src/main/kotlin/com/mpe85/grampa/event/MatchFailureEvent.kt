package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Event posted on a match failure.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
class MatchFailureEvent<T>(context: RuleContext<T>) : RuleContextEvent<T>(context)

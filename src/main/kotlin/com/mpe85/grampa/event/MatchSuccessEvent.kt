package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Event posted on a match success.
 *
 * @author mpe85
 *
 * @param T the type of the stack elements
 */
class MatchSuccessEvent<T>(context: RuleContext<T>) : RuleContextEvent<T>(context)

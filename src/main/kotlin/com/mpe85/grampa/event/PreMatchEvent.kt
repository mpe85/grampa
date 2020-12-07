package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Event posted before a rule match.
 *
 * @author mpe85
 *
 * @param T the type of the stack elements
 */
class PreMatchEvent<T>(context: RuleContext<T>) : RuleContextEvent<T>(context)

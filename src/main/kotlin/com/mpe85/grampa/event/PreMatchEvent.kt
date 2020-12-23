package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Event posted before a rule match.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[context] The rule context in which the event was posted
 */
class PreMatchEvent<T>(context: RuleContext<T>) : RuleContextEvent<T>(context)

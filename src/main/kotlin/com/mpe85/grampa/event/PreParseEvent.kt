package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Event posted before a parse run.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
class PreParseEvent<T>(context: RuleContext<T>) : RuleContextEvent<T>(context)

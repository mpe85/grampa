package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Abstract event posted in the context of a rule.
 *
 * @author mpe85
 * @param T the type of the stack elements
 */
abstract class RuleContextEvent<T>(val context: RuleContext<T>)

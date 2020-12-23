package com.mpe85.grampa.event

import com.mpe85.grampa.rule.RuleContext

/**
 * Abstract event posted in the context of a rule.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[context] The rule context in which the event was posted
 */
abstract class RuleContextEvent<T>(val context: RuleContext<T>)

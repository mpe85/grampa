package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.context.RuleContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that runs one of two sub rules, depending on a condition.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[condition] A condition that is evaluated when the rule is run
 * @property[thenRule] A rule to run if the condition evaluates to true
 * @property[elseRule] An optional rule to run if the condition evaluates to false
 */
class ConditionalRule<T> @JvmOverloads constructor(
  private val condition: (RuleContext<T>) -> Boolean,
  private val thenRule: Rule<T>,
  private val elseRule: Rule<T>? = null
) : AbstractRule<T>(sequenceOf(thenRule, elseRule).filterNotNull().toList()) {

  override fun match(context: ParserContext<T>) =
    if (condition(context)) thenRule.match(context) else elseRule?.match(context) ?: true

  override fun hashCode() = hash(super.hashCode(), condition, thenRule, elseRule)
  override fun equals(other: Any?) =
    checkEquality(other, { super.equals(other) }, { it.condition }, { it.thenRule }, { it.elseRule })

  override fun toString() = stringify(
    "condition" to condition,
    "thenRule" to thenRule::class.simpleName,
    "elseRule" to elseRule?.let { it::class.simpleName }
  )

}

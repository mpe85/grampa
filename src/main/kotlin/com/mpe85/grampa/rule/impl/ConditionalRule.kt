package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash
import java.util.function.Predicate

/**
 * A rule implementation that runs one of two sub rules, depending on a condition.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param condition a condition that is evaluated when the rule is run
 * @param thenRule a rule to run if the condition evaluates to true
 * @param elseRule an optional rule to run if the condition evaluates to false
 */
class ConditionalRule<T> @JvmOverloads constructor(
  private val condition: Predicate<ActionContext<T>>,
  private val thenRule: Rule<T>,
  private val elseRule: Rule<T>? = null
) : AbstractRule<T>(sequenceOf(thenRule, elseRule).filterNotNull().toList()) {

  override fun match(context: RuleContext<T>) =
    if (condition.test(context)) thenRule.match(context) else elseRule?.match(context) ?: true

  override fun hashCode() = hash(super.hashCode(), condition, thenRule, elseRule)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as ConditionalRule<*>
      return (super.equals(other)
          && condition == other.condition
          && thenRule == other.thenRule
          && elseRule == other.elseRule)
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("thenRule", thenRule)
    .add("elseRule", elseRule)

}
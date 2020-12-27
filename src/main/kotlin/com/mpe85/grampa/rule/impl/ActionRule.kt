package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.ParserContext
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * An action rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[action] An action
 * @property[skippable] Indicates if the action is skippable inside test rules
 */
open class ActionRule<T> @JvmOverloads constructor(
  private val action: (RuleContext<T>) -> Boolean,
  private val skippable: Boolean = false
) : AbstractRule<T>() {

  override fun match(context: ParserContext<T>) = context.inTestRule && skippable || action(context)

  override fun hashCode() = hash(super.hashCode(), action, skippable)
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.action }, { it.skippable })
  override fun toString() = stringify("action" to action, "skippable" to skippable)

}

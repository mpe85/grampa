package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.mpe85.grampa.exception.ActionRunException
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * An action rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param action an action
 * @param skippable if the action is skippable inside predicate rules
 */
open class ActionRule<T> @JvmOverloads constructor(
  private val action: Action<T>,
  private val skippable: Boolean = false
) : AbstractRule<T>() {

  override fun match(context: RuleContext<T>) = if (context.inPredicate && skippable) {
    true
  } else try {
    action.run(context)
  } catch (ex: RuntimeException) {
    throw ActionRunException("Failed to run action.", ex)
  }

  override fun hashCode() = hash(super.hashCode(), action, skippable)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as ActionRule<*>
      return super.equals(other)
          && action == other.action
          && skippable == other.skippable
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("skippable", skippable)

}

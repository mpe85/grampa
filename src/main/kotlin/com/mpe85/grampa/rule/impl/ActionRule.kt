package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinHashCode
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.exception.ActionRunException
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.RuleContext

/**
 * An action rule implementation.
 *
 * @author mpe85
 * @param T The type of the stack elements
 * @param action An action
 * @param skippable Indicates if the action is skippable inside predicate rules
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

  override fun hashCode() = kotlinHashCode(properties)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(ActionRule<*>::action, ActionRule<*>::skippable)
  }

}

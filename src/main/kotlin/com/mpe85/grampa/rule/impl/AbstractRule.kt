package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.visitor.RuleVisitor

/**
 * An abstract rule that is base for all rule implementations.
 *
 * @author mpe85
 * @param T The type of the stack elements
 * @param children A list of child rules
 */
abstract class AbstractRule<T>(children: List<Rule<T>> = emptyList()) : Rule<T> {

  private val internalChildren = children.toMutableList()

  /**
   * Construct an abstract rule with one child rule.
   *
   * @param child A child rule
   */
  protected constructor(child: Rule<T>) : this(listOf(child))

  override val children: List<Rule<T>> get() = internalChildren

  override val child: Rule<T>? get() = internalChildren.getOrNull(0)

  override val testRule get() = false

  override fun replaceReferenceRule(index: Int, replacementRule: Rule<T>): Rule<T> {
    require(index in children.indices) { "An 'index' must not be out of bounds." }
    require(children[index] is ReferenceRule<*>) { "Only reference rules can be replaced." }
    return internalChildren.set(index, replacementRule)
  }

  override fun accept(visitor: RuleVisitor<T>) = visitor.visit(this)

}

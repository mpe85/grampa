package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.google.common.base.MoreObjects.toStringHelper
import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.visitor.RuleVisitor
import java.util.Objects.hash

/**
 * An abstract rule that is base for all rule implementations.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param children a list of child rules
 */
abstract class AbstractRule<T>(children: List<Rule<T>> = emptyList()) : Rule<T> {

  private val internalChildren = children.toMutableList()

  /**
   * C'tor. Constructs an abstract rule with one child rule.
   *
   * @param child a child rule
   */
  protected constructor(child: Rule<T>) : this(listOf(child))

  override val children: List<Rule<T>>
    get() = internalChildren

  override val child: Rule<T>?
    get() = children.getOrNull(0)

  override fun replaceReferenceRule(index: Int, replacementRule: Rule<T>): Rule<T> {
    require(index in children.indices) { "An 'index' must not be out of bounds." }
    require(children[index] is ReferenceRule<*>) { "Only reference rules can be replaced." }
    return internalChildren.set(index, replacementRule)
  }

  override val isPredicate: Boolean
    get() = false

  override fun accept(visitor: RuleVisitor<T>) = visitor.visit(this)

  override fun hashCode() = hash(children)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as AbstractRule<*>
      return children == other.children
    }
    return false
  }

  protected open fun toStringHelper(): ToStringHelper = toStringHelper(this)
    .add("#children", children.size)

  override fun toString() = toStringHelper().toString()

}

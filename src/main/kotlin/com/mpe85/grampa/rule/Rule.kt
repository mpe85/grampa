package com.mpe85.grampa.rule

import com.mpe85.grampa.visitor.RuleVisitor

/**
 * Defines a parser rule.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @property children the child rules of the rule
 * @property child the (first) child rule of the rule
 * @property isPredicate if the rule is directly or indirectly part of a predicate rule.
 */
interface Rule<T> {

  val children: List<Rule<T>>
  val child: Rule<T>?
  val isPredicate: Boolean

  /**
   * Replace a reference rule inside the list of child rules.
   *
   * @param index the position if the reference rule inside the list of child rules
   * @param replacementRule a replacement rule
   * @return the replaced reference rule
   */
  fun replaceReferenceRule(index: Int, replacementRule: Rule<T>): Rule<T>

  /**
   * Try to match the rule
   *
   * @param context a rule context
   * @return true if the rule matched successfully, otherwise false
   */
  fun match(context: RuleContext<T>): Boolean


  /**
   * Accepts a rule visitor.
   *
   * @param visitor a rule visitor
   */
  fun accept(visitor: RuleVisitor<T>)

}

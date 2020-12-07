package com.mpe85.grampa.visitor

import com.mpe85.grampa.rule.impl.AbstractRule

/**
 * Visitor for [Rule]s.
 *
 * @author mpe85
 *
 * @param T type of the parser stack values
 */
interface RuleVisitor<T> {

  /**
   * Visits an abstract rule.
   *
   * @param rule the rule to visit
   */
  fun visit(rule: AbstractRule<T>)

}

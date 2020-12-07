package com.mpe85.grampa.visitor.impl

import com.mpe85.grampa.intercept.RuleMethodInterceptor
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.AbstractRule
import com.mpe85.grampa.visitor.RuleVisitor

/**
 * A rule visitor that replaces reference rules with the rules that they reference.
 *
 * @author mpe85
 *
 * @param T type of the parser stack values
 * @param replacementRules A map containing the replacement rules, hashed by the hash code of the rule methods
 *                         they were created by (see [RuleMethodInterceptor]).
 */
class ReferenceRuleReplaceVisitor<T>(private val replacementRules: Map<Int, Rule<T>>) : RuleVisitor<T> {

  override fun visit(rule: AbstractRule<T>) {
    rule.children.forEachIndexed { index, childRule ->
      (childRule as? RuleMethodInterceptor.ReferenceRule<T>)?.let { refRule ->
        rule.replaceReferenceRule(index, replacementRules[refRule.hashCode()])
      } ?: childRule.accept(this)
    }
  }

}

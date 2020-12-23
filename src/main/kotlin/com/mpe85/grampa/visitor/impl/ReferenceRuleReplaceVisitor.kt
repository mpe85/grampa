package com.mpe85.grampa.visitor.impl

import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.AbstractRule
import com.mpe85.grampa.visitor.RuleVisitor

/**
 * A rule visitor that replaces reference rules with the rules that they reference.
 *
 * @author mpe85
 * @param[T] The type of the parser stack values
 * @param[replacementRules] A map containing the replacement rules, hashed by the hash code of the rule methods
 *                          they were created by (see [RuleMethodInterceptor]).
 */
class ReferenceRuleReplaceVisitor<T>(private val replacementRules: Map<Int, Rule<T>?>) : RuleVisitor<T> {


  override fun visit(rule: AbstractRule<T>) {
    rule.children.forEachIndexed { index, childRule ->
      (childRule as? ReferenceRule<T>)?.let { refRule ->
        replacementRules[refRule.referencedRuleHash]?.let { replRule -> rule.replaceReferenceRule(index, replRule) }
      } ?: childRule.accept(this)
    }
  }

}

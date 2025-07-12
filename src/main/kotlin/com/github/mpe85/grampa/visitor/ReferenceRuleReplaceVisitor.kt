package com.github.mpe85.grampa.visitor

import com.github.mpe85.grampa.intercept.RuleMethodInterceptor
import com.github.mpe85.grampa.rule.AbstractRule
import com.github.mpe85.grampa.rule.ReferenceRule
import com.github.mpe85.grampa.rule.Rule

/**
 * A rule visitor that replaces reference rules with the rules that they reference.
 *
 * @param[T] The type of the parser stack values
 * @param[replacementRules] The replacement rules, hashed by the hash code of the rule methods they
 *   were created by, see [RuleMethodInterceptor].
 * @author mpe85
 */
internal class ReferenceRuleReplaceVisitor<T>(private val replacementRules: Map<Int, Rule<T>?>) :
    RuleVisitor<T> {

    override fun visit(rule: AbstractRule<T>) =
        rule.children.forEachIndexed { index, childRule ->
            (childRule as? ReferenceRule<T>)?.let { refRule ->
                replacementRules[refRule.referencedRuleHash]?.let {
                    rule.replaceReferenceRule(index, it)
                }
            } ?: childRule.accept(this)
        }
}

package com.mpe85.grampa.visitor.impl;

import java.util.Map;

import com.mpe85.grampa.intercept.RuleMethodInterceptor;
import com.mpe85.grampa.intercept.RuleMethodInterceptor.ReferenceRule;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.AbstractRule;
import com.mpe85.grampa.visitor.RuleVisitor;

/**
 * A rule visitor that replaces reference rules with the rules that they reference.
 * 
 * @author mpe85
 *
 * @param <T>
 *            type of the parser stack values
 */
public class ReferenceRuleReplaceVisitor<T> implements RuleVisitor<T> {
	
	private final Map<Integer, Rule<T>> replacementRules;
	
	/**
	 * C'tor.
	 * 
	 * @param replacementRules
	 *            A map containing the replacement rules, hashed by the hash code of the rule methods they were created
	 *            by (see {@link RuleMethodInterceptor}).
	 */
	public ReferenceRuleReplaceVisitor(final Map<Integer, Rule<T>> replacementRules) {
		this.replacementRules = replacementRules;
	}
	
	@Override
	public void visit(final AbstractRule<T> rule) {
		for (int i = 0; i < rule.getChildren().size(); i++) {
			final Rule<T> childRule = rule.getChildren().get(i);
			if (childRule instanceof ReferenceRule) {
				final ReferenceRule<T> refRule = (ReferenceRule<T>) childRule;
				rule.replaceReferenceRule(i, replacementRules.get(refRule.hashCode()));
			}
			else {
				childRule.accept(this);
			}
		}
	}
	
}

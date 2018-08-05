package com.mpe85.grampa.visitor.impl;

import java.util.Map;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.AbstractRule;
import com.mpe85.grampa.rule.impl.ReferenceRule;
import com.mpe85.grampa.visitor.RuleVisitor;

public class ReferenceRuleReplaceVisitor<T> implements RuleVisitor<T> {
	
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
	
	
	private final Map<Integer, Rule<T>> replacementRules;
	
}

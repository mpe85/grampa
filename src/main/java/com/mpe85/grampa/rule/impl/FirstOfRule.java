package com.mpe85.grampa.rule.impl;

import java.util.List;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class FirstOfRule<T> extends AbstractRule<T> {
	
	public FirstOfRule(final List<Rule<T>> children) {
		super(children);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		for (final Rule<T> child : getChildren()) {
			if (context.createChildContext(child).run()) {
				return true;
			}
		}
		return false;
	}
	
}

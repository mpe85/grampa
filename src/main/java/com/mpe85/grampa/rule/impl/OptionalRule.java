package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class OptionalRule<T> extends AbstractRule<T> {
	
	public OptionalRule(final Rule<T> rule) {
		super(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.createChildContext(getChild()).run();
		return true;
	}
	
}

package com.mpe85.grampa.rule.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class OptionalRule<T> extends AbstractRule<T> {
	
	public OptionalRule(final Rule<T> matcher) {
		super(Preconditions.checkNotNull(matcher, "A 'matcher' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.createChildContext(getChild()).run();
		return true;
	}
	
}

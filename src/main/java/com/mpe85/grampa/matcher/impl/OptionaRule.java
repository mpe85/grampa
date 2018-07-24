package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.matcher.Rule;
import com.mpe85.grampa.matcher.RuleContext;

public class OptionaRule<T> extends AbstractRule<T> {
	
	public OptionaRule(final Rule<T> matcher) {
		super(Preconditions.checkNotNull(matcher, "A 'matcher' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.getChildContext(getChild()).run();
		return true;
	}
	
}

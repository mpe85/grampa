package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class TestNotRule<T> extends AbstractRule<T> {
	
	public TestNotRule(final Rule<T> rule) {
		super(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.getStack().takeSnapshot();
		
		if (context.createChildContext(getChild()).run()) {
			context.getStack().restoreSnapshot();
			return false;
		}
		return true;
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}

package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class TestNotRule<T> extends AbstractRule<T> {
	
	public TestNotRule(final Rule<T> matcher) {
		super(matcher);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final int currentIndex = context.getCurrentIndex();
		context.getStack().takeSnapshot();
		
		if (!context.getChildContext(getChild()).run()) {
			context.setCurrentIndex(currentIndex);
			context.getStack().restoreSnapshot();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}

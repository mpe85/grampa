package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class TestRule<T> extends AbstractRule<T> {
	
	public TestRule(final Rule<T> matcher) {
		super(matcher);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final int currentIndex = context.getCurrentIndex();
		context.getStack().takeSnapshot();
		
		if (context.createChildContext(getChild()).run()) {
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

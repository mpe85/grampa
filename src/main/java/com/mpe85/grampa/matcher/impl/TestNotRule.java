package com.mpe85.grampa.matcher.impl;

import com.mpe85.grampa.matcher.Rule;
import com.mpe85.grampa.matcher.RuleContext;

public class TestNotRule<T> extends AbstractRule<T> {
	
	public TestNotRule(final Rule<T> matcher) {
		super(matcher);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final int currentIndex = context.getCurrentIndex();
		context.getValueStack().takeSnapshot();
		
		if (!context.getChildContext(getChild()).run()) {
			context.setCurrentIndex(currentIndex);
			context.getValueStack().restoreSnapshot();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}

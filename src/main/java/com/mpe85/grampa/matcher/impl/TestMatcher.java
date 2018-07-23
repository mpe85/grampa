package com.mpe85.grampa.matcher.impl;

import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;

public class TestMatcher<T> extends AbstractMatcher<T> {
	
	public TestMatcher(final IMatcher<T> matcher) {
		super(matcher);
	}
	
	@Override
	public boolean match(final IMatcherContext<T> context) {
		final int currentIndex = context.getCurrentIndex();
		context.getValueStack().takeSnapshot();
		
		if (context.getChildContext(getChild()).run()) {
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

package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;

public class OptionalMatcher<T> extends AbstractMatcher<T> {
	
	public OptionalMatcher(final IMatcher<T> matcher) {
		super(Preconditions.checkNotNull(matcher, "A 'matcher' must not be null."));
	}
	
	@Override
	public boolean match(final IMatcherContext<T> context) {
		context.getChildContext(getChild()).run();
		return true;
	}
	
}

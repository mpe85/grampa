package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.matcher.IMatcherContext;

public class StringMatcher extends AbstractMatcher {
	
	public StringMatcher(
			final String string,
			final boolean predicate) {
		super(predicate);
		Preconditions.checkNotNull(string, "A 'string' must not be null.");
		this.string = string;
	}
	
	@Override
	public boolean match(final IMatcherContext context) {
		if (context.getNumberOfCharsLeft() >= string.length()) {
			final CharSequence nextChars = context.getInputBuffer().subSequence(
					context.getCurrentIndex(),
					context.getCurrentIndex() + string.length());
			if (string.equals(nextChars.toString())) {
				return context.advanceIndex(string.length());
			}
		}
		return false;
	}
	
	
	private final String string;
	
}

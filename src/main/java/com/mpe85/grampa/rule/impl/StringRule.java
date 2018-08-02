package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.rule.RuleContext;

public class StringRule<T> extends AbstractRule<T> {
	
	public StringRule(final String string) {
		Preconditions.checkNotNull(string, "A 'string' must not be null.");
		this.string = string;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (context.getNumberOfCharsLeft() >= string.length()) {
			final CharSequence nextChars = context.getInputBuffer().subSequence(
					context.getCurrentIndex(),
					context.getCurrentIndex() + string.length());
			return string.equals(nextChars.toString()) && context.advanceIndex(string.length());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), string);
	}
	
	
	private final String string;
	
}

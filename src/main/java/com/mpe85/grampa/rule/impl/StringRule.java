package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
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
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final StringRule<?> other = (StringRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(string, other.string);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("string", string);
	}
	
	
	private final String string;
	
}

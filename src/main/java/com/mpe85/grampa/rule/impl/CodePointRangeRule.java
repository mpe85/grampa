package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

public class CodePointRangeRule<T> extends AbstractRule<T> {
	
	public CodePointRangeRule(final int lowerBound, final int upperBound) {
		checkArgument(lowerBound <= upperBound, "A 'lowerBound' must not be greater than an 'upperBound'.");
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& context.getCurrentCodePoint() >= lowerBound
				&& context.getCurrentCodePoint() <= upperBound
				&& context.advanceIndex(Character.charCount(context.getCurrentCodePoint()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), lowerBound, upperBound);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final CodePointRangeRule<?> other = (CodePointRangeRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(lowerBound, other.lowerBound)
					&& Objects.equals(upperBound, other.upperBound);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("lowerBound", lowerBound)
				.add("upperBound", upperBound);
	}
	
	
	private final int lowerBound;
	private final int upperBound;
}

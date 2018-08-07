package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

public class AnyOfCodePointsRule<T> extends AbstractRule<T> {
	
	public AnyOfCodePointsRule(final Set<Integer> codePoints) {
		this(codePoints, false);
	}
	
	public AnyOfCodePointsRule(
			final Set<Integer> codePoints,
			final boolean negated) {
		this.codePoints = checkNotNull(codePoints, "A set of 'codePoints' must not be null.");
		this.negated = negated;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& codePoints.contains(context.getCurrentCodePoint()) != negated
				&& context.advanceIndex(Character.charCount(context.getCurrentCodePoint()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), codePoints);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final AnyOfCodePointsRule<?> other = (AnyOfCodePointsRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(codePoints, other.codePoints);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("codePoints", codePoints);
	}
	
	
	private final Set<Integer> codePoints;
	private final boolean negated;
	
}

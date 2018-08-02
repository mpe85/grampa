package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.mpe85.grampa.rule.RuleContext;

public class CodePointRule<T> extends AbstractRule<T> {
	
	public CodePointRule(final int codePoint) {
		this(codePoint, false);
	}
	
	public CodePointRule(
			final int codePoint,
			final boolean ignoreCase) {
		this.codePoint = codePoint;
		this.ignoreCase = ignoreCase;
	}
	
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& isSame(context.getCurrentCodePoint())
				&& context.advanceIndex(Character.charCount(codePoint));
	}
	
	private boolean isSame(final int currentCodePoint) {
		return ignoreCase
				? currentCodePoint == Character.toLowerCase(codePoint)
						|| currentCodePoint == Character.toUpperCase(codePoint)
				: currentCodePoint == codePoint;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), codePoint, ignoreCase);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final CodePointRule<?> other = (CodePointRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(codePoint, other.codePoint)
					&& Objects.equals(ignoreCase, other.ignoreCase);
		}
		return false;
	}
	
	
	private final int codePoint;
	private final boolean ignoreCase;
	
}

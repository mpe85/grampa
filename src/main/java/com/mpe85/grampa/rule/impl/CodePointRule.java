package com.mpe85.grampa.rule.impl;

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
	
	
	private final int codePoint;
	private final boolean ignoreCase;
	
}

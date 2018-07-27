package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

public class AnyCodePointRule<T> extends AbstractRule<T> {
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (!context.isAtEndOfInput()) {
			final int codePoint = context.getCurrentCodePoint();
			return Character.isValidCodePoint(codePoint) && context.advanceIndex(Character.charCount(codePoint));
		}
		return false;
	}
	
}

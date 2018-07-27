package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

public class CodePointRule<T> extends AbstractRule<T> {
	
	public CodePointRule(final int codePoint) {
		this.codePoint = codePoint;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& context.getCurrentCodePoint() == codePoint
				&& context.advanceIndex(Character.charCount(codePoint));
	}
	
	
	private final int codePoint;
	
}

package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

public class CharRule<T> extends AbstractRule<T> {
	
	public CharRule(final char character) {
		this.character = character;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (!context.isAtEndOfInput() && context.getCurrentChar() == character) {
			context.advanceIndex(1);
			return true;
		}
		return false;
	}
	
	
	private final char character;
	
}

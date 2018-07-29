package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

public class CharRule<T> extends AbstractRule<T> {
	
	public CharRule(final char character) {
		this(character, false);
	}
	
	public CharRule(
			final char character,
			final boolean ignoreCase) {
		this.character = character;
		this.ignoreCase = ignoreCase;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& isSame(context.getCurrentChar())
				&& context.advanceIndex(1);
	}
	
	private boolean isSame(final char currentChar) {
		return ignoreCase
				? currentChar == Character.toLowerCase(character) || currentChar == Character.toUpperCase(character)
				: currentChar == character;
	}
	
	
	private final char character;
	private final boolean ignoreCase;
	
}

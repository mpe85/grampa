package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
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
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), character, ignoreCase);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final CharRule<?> other = (CharRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(character, other.character)
					&& Objects.equals(ignoreCase, other.ignoreCase);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("character", character)
				.add("ignoreCase", ignoreCase);
	}
	
	
	private final char character;
	private final boolean ignoreCase;
	
}

package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Set;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

public class AnyOfRule<T> extends AbstractRule<T> {
	
	public AnyOfRule(final Set<Character> characters) {
		this(characters, false);
	}
	
	public AnyOfRule(
			final Set<Character> characters,
			final boolean negated) {
		this.characters = checkNotNull(characters, "A set of 'characters' must not be null.");
		this.negated = negated;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& characters.contains(context.getCurrentChar()) != negated
				&& context.advanceIndex(1);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), characters);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final AnyOfRule<?> other = (AnyOfRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(characters, other.characters);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("characters", characters);
	}
	
	
	private final Set<Character> characters;
	private final boolean negated;
	
}

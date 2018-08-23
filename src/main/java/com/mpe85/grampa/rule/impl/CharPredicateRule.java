package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A character predicate rule implementation.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class CharPredicateRule<T> extends AbstractRule<T> {
	
	/**
	 * C'tor. Create a character predicate rules that exactly matches a specific character.
	 * 
	 * @param character
	 *            a character
	 */
	public CharPredicateRule(final char character) {
		this(c -> c == character);
	}
	
	/**
	 * C'tor. Create a character predicate rule.
	 * 
	 * @param predicate
	 *            that is tested by the rule.
	 */
	public CharPredicateRule(final Predicate<Character> predicate) {
		this.predicate = checkNotNull(predicate, "A 'predicate' must not be null.");
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& predicate.test(context.getCurrentChar())
				&& context.advanceIndex(1);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), predicate);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final CharPredicateRule<?> other = (CharPredicateRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(predicate, other.predicate);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("predicate", predicate);
	}
	
	
	private final Predicate<Character> predicate;
	
}

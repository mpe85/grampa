package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.ibm.icu.lang.UCharacter;
import com.mpe85.grampa.rule.RuleContext;

public class CodePointPredicateRule<T> extends AbstractRule<T> {
	
	public CodePointPredicateRule(final Predicate<Integer> predicate) {
		this.predicate = checkNotNull(predicate, "A 'predicate' must not be null.");
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.isAtEndOfInput()
				&& predicate.test(context.getCurrentCodePoint())
				&& context.advanceIndex(UCharacter.charCount(context.getCurrentCodePoint()));
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), predicate);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final CodePointPredicateRule<?> other = (CodePointPredicateRule<?>) obj;
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
	
	
	private final Predicate<Integer> predicate;
	
}
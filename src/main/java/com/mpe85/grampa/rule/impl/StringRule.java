package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.ibm.icu.lang.UCharacter;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A string rule implementation.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class StringRule<T> extends AbstractRule<T> {
	
	/**
	 * C'tor. Constructs a string rules that exactly matches a string (case-sensitive).
	 * 
	 * @param string
	 *            a string
	 */
	public StringRule(final String string) {
		this(string, false);
	}
	
	/**
	 * C'tor. Constructs a string rules that matches a string (case-sensitive or case-insensitive).
	 * 
	 * @param string
	 *            a string
	 * @param ignoreCase
	 *            if true, the case is ignored
	 */
	public StringRule(
			final String string,
			final boolean ignoreCase) {
		checkNotNull(string, "A 'string' must not be null.");
		this.string = ignoreCase ? UCharacter.toLowerCase(string) : string;
		this.ignoreCase = ignoreCase;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (context.getNumberOfCharsLeft() >= string.length()) {
			final CharSequence nextChars = context.getInputBuffer().subSequence(
					context.getCurrentIndex(),
					context.getCurrentIndex() + string.length());
			return string.equals(ignoreCase
					? UCharacter.toLowerCase(nextChars.toString())
					: nextChars.toString())
					&& context.advanceIndex(string.length());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), string, ignoreCase);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final StringRule<?> other = (StringRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(string, other.string)
					&& Objects.equals(ignoreCase, other.ignoreCase);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("string", string)
				.add("ignoreCase", ignoreCase);
	}
	
	
	private final String string;
	private final boolean ignoreCase;
	
}

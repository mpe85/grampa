package com.mpe85.grampa.rule.impl;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A regular expression rule implementation.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class RegexRule<T> extends AbstractRule<T> {
	
	private final Pattern pattern;
	
	/**
	 * C'tor. Constructs a regex rule using an already compiled {@link Pattern}.
	 * 
	 * @param pattern
	 *            a compiled regular expression
	 */
	public RegexRule(final Pattern pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * C'tor. Constructs a regex rule using a regex string.
	 * 
	 * @param regex
	 *            a string containing a regular expression
	 */
	public RegexRule(final String regex) {
		pattern = Pattern.compile(regex);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final Matcher regexMatcher = pattern.matcher(context.getRestOfInput());
		return regexMatcher.lookingAt() && context.advanceIndex(regexMatcher.end());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), pattern);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final RegexRule<?> other = (RegexRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(pattern, other.pattern);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("pattern", pattern);
	}
	
}

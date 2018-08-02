package com.mpe85.grampa.rule.impl;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mpe85.grampa.rule.RuleContext;

public class RegexRule<T> extends AbstractRule<T> {
	
	
	public RegexRule(final Pattern pattern) {
		this.pattern = pattern;
	}
	
	public RegexRule(final String regex) {
		pattern = Pattern.compile(regex);
	}
	
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final Matcher matcher = pattern.matcher(context.getRestOfInput());
		return matcher.lookingAt() && context.advanceIndex(matcher.end());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), pattern);
	}
	
	private final Pattern pattern;
	
}

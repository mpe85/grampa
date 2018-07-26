package com.mpe85.grampa.rule.impl;

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
		
		final boolean matched = matcher.lookingAt();
		
		if (matched) {
			context.advanceIndex(matcher.end());
		}
		
		return matched;
	}
	
	private final Pattern pattern;
	
}

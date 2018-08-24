package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

/**
 * A rule implementation that matches an empty input.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class EmptyRule<T> extends AbstractRule<T> {
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return true;
	}
	
}

package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

/**
 * A rule implementation that never matches.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class NeverRule<T> extends AbstractRule<T> {
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return false;
	}
	
}

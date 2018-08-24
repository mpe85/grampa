package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

/**
 * A rule implementation that matches the end of the input.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class EndOfInputRule<T> extends AbstractRule<T> {
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return context.isAtEndOfInput();
	}
	
}

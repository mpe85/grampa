package com.mpe85.grampa.rule.impl;

import com.mpe85.grampa.rule.RuleContext;

public class EndOfInputRule<T> extends AbstractRule<T> {
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return context.isAtEndOfInput();
	}
	
}

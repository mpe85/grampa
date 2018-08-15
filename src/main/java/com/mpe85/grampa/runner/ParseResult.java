package com.mpe85.grampa.runner;

import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;

public class ParseResult<T> {
	
	public ParseResult(final boolean matched, final RuleContext<T> context) {
		this.matched = matched;
		this.matchedWholeInput = matched && context.isAtEndOfInput();
		this.valueStack = context.getStack().copy();
	}
	
	public boolean isMatched() {
		return matched;
	}
	
	public boolean isMatchedWholeInput() {
		return matchedWholeInput;
	}
	
	public RestorableStack<T> getValueStack() {
		return valueStack;
	}
	
	public T getValueStackTop() {
		return valueStack.size() > 0 ? valueStack.peek() : null;
	}
	
	
	private final boolean matched;
	private final boolean matchedWholeInput;
	private final RestorableStack<T> valueStack;
	
}

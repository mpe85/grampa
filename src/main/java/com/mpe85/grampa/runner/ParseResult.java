package com.mpe85.grampa.runner;

import com.mpe85.grampa.matcher.RuleContext;
import com.mpe85.grampa.util.stack.IRestorableStack;

public class ParseResult<T> {
	
	@SuppressWarnings("unchecked")
	public ParseResult(final boolean matched, final RuleContext<T> context) {
		this.matched = matched;
		this.matchedWholeInput = matched && context.isAtEndOfInput();
		this.valueStack = (IRestorableStack<T>) context.getValueStack().clone();
	}
	
	public boolean isMatched() {
		return matched;
	}
	
	public boolean isMatchedWholeInput() {
		return matchedWholeInput;
	}
	
	public IRestorableStack<T> getValueStack() {
		return valueStack;
	}
	
	public T getValueStackTop() {
		return valueStack.size() > 0 ? valueStack.peek() : null;
	}
	
	final boolean matched;
	final boolean matchedWholeInput;
	final IRestorableStack<T> valueStack;
	
}

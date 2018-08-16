package com.mpe85.grampa.runner;

import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;

public class ParseResult<T> {
	
	public ParseResult(final boolean matched, final RuleContext<T> context) {
		this.matched = matched;
		this.matchedWholeInput = matched && context.isAtEndOfInput();
		this.matchedInput = matched ? context.getMatchedInput() : null;
		this.restOfInput = matched ? context.getRestOfInput() : context.getInput();
		this.stack = context.getStack().copy();
	}
	
	public boolean isMatched() {
		return matched;
	}
	
	public boolean isMatchedWholeInput() {
		return matchedWholeInput;
	}
	
	public CharSequence getMatchedInput() {
		return matchedInput;
	}
	
	public CharSequence getRestOfInput() {
		return restOfInput;
	}
	
	public RestorableStack<T> getStack() {
		return stack;
	}
	
	public T getStackTop() {
		return stack.size() > 0 ? stack.peek() : null;
	}
	
	
	private final boolean matched;
	private final boolean matchedWholeInput;
	private final CharSequence matchedInput;
	private final CharSequence restOfInput;
	private final RestorableStack<T> stack;
	
}

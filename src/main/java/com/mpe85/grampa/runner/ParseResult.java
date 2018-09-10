package com.mpe85.grampa.runner;

import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;

/**
 * Holds the result of a parse run.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class ParseResult<T> {
	
	/**
	 * C'tor.
	 * 
	 * @param matched
	 *            if the parse run matched successfully
	 * @param context
	 *            the root context after the parse run
	 */
	public ParseResult(final boolean matched, final RuleContext<T> context) {
		this.matched = matched;
		this.matchedWholeInput = matched && context.isAtEndOfInput();
		this.matchedInput = matched ? context.getMatchedInput() : null;
		this.restOfInput = matched ? context.getRestOfInput() : context.getInput();
		this.stack = context.getStack().copy();
	}
	
	/**
	 * Checks if the parse run matched the input successfully. This also returns true if there was a partial match.
	 * 
	 * @return true if it matched, false otherwise
	 */
	public boolean isMatched() {
		return matched;
	}
	
	/**
	 * Checks if the parse run matched the whole input successfully.
	 * 
	 * @return true if it matched, false otherwise
	 */
	public boolean isMatchedWholeInput() {
		return matchedWholeInput;
	}
	
	/**
	 * Get the part of the input that matched successfully.
	 * 
	 * @return a character sequence
	 */
	public CharSequence getMatchedInput() {
		return matchedInput;
	}
	
	/**
	 * Get the rest of the input that did not match successfully.
	 * 
	 * @return a character sequence
	 */
	public CharSequence getRestOfInput() {
		return restOfInput;
	}
	
	/**
	 * Gets the parser's stack.
	 * 
	 * @return a stack
	 */
	public RestorableStack<T> getStack() {
		return stack;
	}
	
	/**
	 * Get the top element of the parser's stack.
	 * 
	 * @return a stack element, or null if the stack is empty
	 */
	public T getStackTop() {
		return stack.size() > 0 ? stack.peek() : null;
	}
	
	
	private final boolean matched;
	private final boolean matchedWholeInput;
	private final CharSequence matchedInput;
	private final CharSequence restOfInput;
	private final RestorableStack<T> stack;
	
}

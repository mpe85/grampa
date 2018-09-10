package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputBuffer;

/**
 * Defines a context for parser rules.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public interface RuleContext<T> extends ActionContext<T> {
	
	/**
	 * Gets the underlying input buffer.
	 * 
	 * @return an input buffer
	 */
	InputBuffer getInputBuffer();
	
	/**
	 * Sets the current index inside the parser input.
	 * 
	 * @param currentIndex
	 *            a valid index
	 */
	void setCurrentIndex(int currentIndex);
	
	/**
	 * Advances the current index inside the parser input.
	 * 
	 * @param delta
	 *            number of characters to advance
	 * @return true if the advancing was possible, otherwise false
	 */
	boolean advanceIndex(int delta);
	
	/**
	 * Runs the parser against the input.
	 * 
	 * @return true if the parser successfully matched the input, otherwise false
	 */
	boolean run();
	
	@Override
	RuleContext<T> getParent();
	
	/**
	 * Creates a child context out of the context that is used to run child rules.
	 * 
	 * @param rule
	 *            a child rule for which the child context should be created for
	 * @return a rule contexts
	 */
	RuleContext<T> createChildContext(Rule<T> rule);
	
}

package com.mpe85.grampa.rule;

/**
 * A supplier for parser stack values.
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
@FunctionalInterface
public interface ValueSupplier<T> {
	
	/**
	 * Supplies a value for the parser stack.
	 * 
	 * @param context
	 *                an action context
	 * @return a stack value
	 */
	T supply(ActionContext<T> context);
	
}

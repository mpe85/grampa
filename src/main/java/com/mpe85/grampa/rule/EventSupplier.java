package com.mpe85.grampa.rule;

/**
 * A supplier for parser events.
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
@FunctionalInterface
public interface EventSupplier<T> {
	
	/**
	 * Supplies a parser event.
	 * 
	 * @param context
	 *        an action context
	 * @return an event
	 */
	Object supply(ActionContext<T> context);
	
}

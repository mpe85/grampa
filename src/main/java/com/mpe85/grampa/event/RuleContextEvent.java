package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

/**
 * Abstract event posted in the context of a rule.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public abstract class RuleContextEvent<T> {
	
	public RuleContextEvent(final RuleContext<T> context) {
		this.context = context;
	}
	
	public RuleContext<T> getContext() {
		return context;
	}
	
	protected final RuleContext<T> context;
	
}

package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

/**
 * Event posted on a match failure.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class MatchFailureEvent<T> extends RuleContextEvent<T> {
	
	public MatchFailureEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

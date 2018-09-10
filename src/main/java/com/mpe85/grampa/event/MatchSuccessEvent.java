package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

/**
 * Event posted on a match success.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class MatchSuccessEvent<T> extends RuleContextEvent<T> {
	
	public MatchSuccessEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

/**
 * Event posted before a rule match.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class PreMatchEvent<T> extends RuleContextEvent<T> {
	
	public PreMatchEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

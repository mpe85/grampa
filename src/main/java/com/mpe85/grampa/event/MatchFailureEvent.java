package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

public class MatchFailureEvent<T> extends RuleContextEvent<T> {
	
	public MatchFailureEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

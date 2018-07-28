package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

public class MatchSuccessEvent<T> extends RuleContextEvent<T> {
	
	public MatchSuccessEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

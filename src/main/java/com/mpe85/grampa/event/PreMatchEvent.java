package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

public class PreMatchEvent<T> extends RuleContextEvent<T> {
	
	public PreMatchEvent(final RuleContext<T> context) {
		super(context);
	}
	
}

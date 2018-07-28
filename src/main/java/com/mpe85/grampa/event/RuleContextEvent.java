package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

public abstract class RuleContextEvent<T> {
	
	public RuleContextEvent(final RuleContext<T> context) {
		this.context = context;
	}
	
	public RuleContext<T> getContext() {
		return context;
	}
	
	protected final RuleContext<T> context;
	
}

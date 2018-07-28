package com.mpe85.grampa.event;

import com.mpe85.grampa.rule.RuleContext;

public class PreParseEvent<T> extends RuleContextEvent<T> {
	
	public PreParseEvent(final RuleContext<T> context) {
		super(context);
	}
	
	
}

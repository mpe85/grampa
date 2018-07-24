package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.matcher.Action;
import com.mpe85.grampa.matcher.RuleContext;

public class ActionRule<T> extends AbstractRule<T> {
	
	public ActionRule(final Action<T> action) {
		this.action = Preconditions.checkNotNull(action, "An 'action' must not be null.");
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.getValueStack().takeSnapshot();
		final boolean matched = action.run(context);
		context.getValueStack().removeSnapshot(!matched);
		return matched;
	}
	
	private final Action<T> action;
	
}

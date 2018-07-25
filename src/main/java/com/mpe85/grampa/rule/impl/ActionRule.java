package com.mpe85.grampa.rule.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.RuleContext;

public class ActionRule<T> extends AbstractRule<T> {
	
	public ActionRule(final Action<T> action) {
		this(action, false);
	}
	
	public ActionRule(
			final Action<T> action,
			final boolean skippable) {
		this.action = Preconditions.checkNotNull(action, "An 'action' must not be null.");
		this.skippable = skippable;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (context.inPredicate() && skippable) {
			return true;
		}
		context.getValueStack().takeSnapshot();
		final boolean matched = action.run(context);
		context.getValueStack().removeSnapshot(!matched);
		return matched;
	}
	
	private final Action<T> action;
	private final boolean skippable;
	
}

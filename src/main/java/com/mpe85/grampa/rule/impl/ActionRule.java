package com.mpe85.grampa.rule.impl;

import java.util.Objects;

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
		context.getStack().takeSnapshot();
		final boolean matched = action.run(context);
		context.getStack().removeSnapshot(!matched);
		return matched;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), action, skippable);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final ActionRule<?> other = (ActionRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(action, other.action)
					&& Objects.equals(skippable, other.skippable);
		}
		return false;
	}
	
	
	private final Action<T> action;
	private final boolean skippable;
	
}

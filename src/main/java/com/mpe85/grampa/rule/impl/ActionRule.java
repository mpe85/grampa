package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.exception.ActionRunException;
import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.RuleContext;

public class ActionRule<T> extends AbstractRule<T> {
	
	public ActionRule(final Action<T> action) {
		this(action, false);
	}
	
	public ActionRule(
			final Action<T> action,
			final boolean skippable) {
		this.action = checkNotNull(action, "An 'action' must not be null.");
		this.skippable = skippable;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		if (context.inPredicate() && skippable) {
			return true;
		}
		try {
			return action.run(context);
		}
		catch (final RuntimeException ex) {
			throw new ActionRunException("Failed to run action.", ex);
		}
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
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("action", action)
				.add("skippable", skippable);
	}
	
	
	private final Action<T> action;
	private final boolean skippable;
	
}

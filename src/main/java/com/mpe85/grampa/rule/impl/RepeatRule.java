package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

public class RepeatRule<T> extends AbstractRule<T> {
	
	public RepeatRule(
			final Rule<T> rule,
			final int min,
			final Integer max) {
		super(checkNotNull(rule, "A 'rule' must not be null."));
		checkArgument(min >= 0, "A 'min' number must not be negative");
		if (max != null) {
			checkArgument(max >= min, "A 'max' number must not be lower than the 'min' number.");
		}
		this.min = min;
		this.max = max;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		int iterations = 0;
		while (max == null || iterations < max) {
			if (!context.createChildContext(getChild()).run()) {
				break;
			}
			iterations++;
		}
		return iterations >= min;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), min, max);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final RepeatRule<?> other = (RepeatRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(min, other.min)
					&& Objects.equals(max, other.max);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("min", min)
				.add("max", max);
	}
	
	
	private final int min;
	private final Integer max;
	
}

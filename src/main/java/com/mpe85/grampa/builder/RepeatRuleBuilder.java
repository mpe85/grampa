package com.mpe85.grampa.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.RepeatRule;

public class RepeatRuleBuilder<T> {
	
	public RepeatRuleBuilder(final Rule<T> rule) {
		this.rule = checkNotNull(rule, " A 'rule' must not be null.");
	}
	
	public RepeatRule<T> times(final int number) {
		return new RepeatRule<>(rule, number, number);
	}
	
	public RepeatRule<T> times(final int min, final int max) {
		return new RepeatRule<>(rule, min, max);
	}
	
	public RepeatRule<T> min(final int min) {
		return new RepeatRule<>(rule, min, null);
	}
	
	public RepeatRule<T> max(final int max) {
		return new RepeatRule<>(rule, 0, max);
	}
	
	private final Rule<T> rule;
	
}

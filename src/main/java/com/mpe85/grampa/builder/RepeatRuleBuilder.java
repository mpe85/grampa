package com.mpe85.grampa.builder;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.RepeatRule;

/**
 * A builder for repeat rules. See {@link RepeatRule}.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class RepeatRuleBuilder<T> {
	
	private final Rule<T> rule;
	
	/**
	 * C'tor.
	 * 
	 * @param rule
	 *            the repeated rule
	 */
	public RepeatRuleBuilder(final Rule<T> rule) {
		this.rule = checkNotNull(rule, " A 'rule' must not be null.");
	}
	
	/**
	 * Repeats the rule exactly n times.
	 * 
	 * @param number
	 *            the number specifying how many times the rule is repeated
	 * @return a repeat rule
	 */
	public RepeatRule<T> times(final int number) {
		return new RepeatRule<>(rule, number, number);
	}
	
	/**
	 * Repeats the rule between n and m times.
	 * 
	 * @param min
	 *            the number specifying how many times the rule must be repeated at least
	 * @param max
	 *            the number specifying how many times the rule can be repeated at most
	 * @return a repeat rule
	 */
	public RepeatRule<T> times(final int min, final int max) {
		return new RepeatRule<>(rule, min, max);
	}
	
	/**
	 * Repeats the rule at least n times.
	 * 
	 * @param min
	 *            the number specifying how many times the rule must be repeated at least
	 * @return a repeat rule
	 */
	public RepeatRule<T> min(final int min) {
		return new RepeatRule<>(rule, min, null);
	}
	
	/**
	 * Repeats the rule at most n times.
	 * 
	 * @param max
	 *            the number specifying how many times the rule can be repeated at most
	 * @return a repeat rule
	 */
	public RepeatRule<T> max(final int max) {
		return new RepeatRule<>(rule, 0, max);
	}
	
}

package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.ActionContext;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

import one.util.streamex.StreamEx;

/**
 * A rule implementation that runs one of two sub rules, depending on a condition.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class ConditionalRule<T> extends AbstractRule<T> {
	
	private final Predicate<ActionContext<T>> condition;
	private final Rule<T> thenRule;
	private final Rule<T> elseRule;
	
	/**
	 * C'tor.
	 * 
	 * @param condition
	 *            a condition that is evaluated when the rule is run
	 * @param thenRule
	 *            a rule to run if the condition evaluates to true
	 */
	public ConditionalRule(
			final Predicate<ActionContext<T>> condition,
			final Rule<T> thenRule) {
		this(condition, thenRule, null);
	}
	
	/**
	 * C'tor.
	 * 
	 * @param condition
	 *            a condition that is evaluated when the rule is run
	 * @param thenRule
	 *            a rule to run if the condition evaluates to true
	 * @param elseRule
	 *            an optional rule to run if the condition evaluates to false
	 */
	public ConditionalRule(
			final Predicate<ActionContext<T>> condition,
			final Rule<T> thenRule,
			final Rule<T> elseRule) {
		super(StreamEx.of(thenRule, elseRule).nonNull().toList());
		this.condition = checkNotNull(condition, "A 'condition' must not be null.");
		this.thenRule = checkNotNull(thenRule, "A 'thenRule' must not be null.");
		this.elseRule = elseRule;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return condition.test(context)
				? thenRule.match(context)
				: Optional.ofNullable(elseRule).map(r -> r.match(context)).orElse(true);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), condition, thenRule, elseRule);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final ConditionalRule<?> other = (ConditionalRule<?>) obj;
			return super.equals(other)
					&& Objects.equals(condition, other.condition)
					&& Objects.equals(thenRule, other.thenRule)
					&& Objects.equals(elseRule, other.elseRule);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("thenRule", thenRule)
				.add("elseRule", elseRule);
	}
	
}

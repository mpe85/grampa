package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A predicate rule implementation that tests if its child rule does not match.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class TestNotRule<T> extends AbstractRule<T> {
	
	/**
	 * C'tor.
	 * 
	 * @param rule
	 *            the child rule to test
	 */
	public TestNotRule(final Rule<T> rule) {
		super(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return !context.createChildContext(getChild()).run();
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}

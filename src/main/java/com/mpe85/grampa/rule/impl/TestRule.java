package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A predicate rule implementation that tests if its child rule matches.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class TestRule<T> extends AbstractRule<T> {
	
	/**
	 * C'tor.
	 * 
	 * @param rule
	 *            the child rule to test
	 */
	public TestRule(final Rule<T> rule) {
		super(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		final int currentIndex = context.getCurrentIndex();
		context.getStack().takeSnapshot();
		
		if (context.createChildContext(getChild()).run()) {
			// reset current index and stack
			context.setCurrentIndex(currentIndex);
			context.getStack().restoreSnapshot();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean isPredicate() {
		return true;
	}
	
}

package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A rule implementation that matches the first successful rule of its child rules.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class FirstOfRule<T> extends AbstractRule<T> {
	
	/**
	 * C'tor.
	 * 
	 * @param rules
	 *            a list of child rules.
	 */
	public FirstOfRule(final List<Rule<T>> rules) {
		super(checkNotNull(rules, "A list of 'rules' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		for (final Rule<T> child : getChildren()) {
			if (context.createChildContext(child).run()) {
				return true;
			}
		}
		return false;
	}
	
}

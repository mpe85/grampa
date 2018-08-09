package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;

import one.util.streamex.StreamEx;

public class SequenceRule<T> extends AbstractRule<T> {
	
	public SequenceRule(final List<Rule<T>> rules) {
		super(checkNotNull(rules, "A list of 'rules' must not be null."));
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.getStack().takeSnapshot();
		final boolean matched = StreamEx.of(getChildren()).allMatch(c -> context.createChildContext(c).run());
		context.getStack().removeSnapshot(!matched);
		return matched;
	}
	
}

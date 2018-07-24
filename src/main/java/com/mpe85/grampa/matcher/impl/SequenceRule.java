package com.mpe85.grampa.matcher.impl;

import java.util.List;

import com.mpe85.grampa.matcher.Rule;
import com.mpe85.grampa.matcher.RuleContext;

import one.util.streamex.StreamEx;

public class SequenceRule<T> extends AbstractRule<T> {
	
	public SequenceRule(final List<Rule<T>> children) {
		super(children);
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		context.getValueStack().takeSnapshot();
		final boolean matched = StreamEx.of(getChildren()).allMatch(c -> context.getChildContext(c).run());
		context.getValueStack().removeSnapshot(!matched);
		return matched;
	}
	
}

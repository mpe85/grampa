package com.mpe85.grampa.matcher.impl;

import java.util.List;

import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;

import one.util.streamex.StreamEx;

public class SequenceMatcher<T> extends AbstractMatcher<T> {
	
	protected SequenceMatcher(final List<IMatcher<T>> children) {
		super(children);
	}
	
	@Override
	public boolean match(final IMatcherContext<T> context) {
		context.getValueStack().takeSnapshot();
		
		if (StreamEx.of(getChildren()).allMatch(c -> context.getChildContext(c).run())) {
			context.getValueStack().discardSnapshot();
			return true;
		}
		else {
			context.getValueStack().restoreSnapshot();
			return false;
		}
	}
	
}

package com.mpe85.grampa.matcher.impl;

import java.util.Collections;
import java.util.List;

import com.mpe85.grampa.matcher.IMatcher;

public abstract class AbstractMatcher implements IMatcher {
	
	protected AbstractMatcher(final boolean predicate) {
		this(Collections.emptyList(), predicate);
	}
	
	protected AbstractMatcher(
			final List<IMatcher> children,
			final boolean predicate) {
		super();
		this.children = children;
		this.predicate = predicate;
	}
	
	@Override
	public List<IMatcher> getChildren() {
		return children;
	}
	
	@Override
	public boolean isPredicate() {
		return predicate;
	}
	
	
	private final List<IMatcher> children;
	private final boolean predicate;
	
}

package com.mpe85.grampa.matcher.impl;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.matcher.IMatcher;

public abstract class AbstractMatcher<T> implements IMatcher<T> {
	
	protected AbstractMatcher() {
		this(Collections.emptyList());
	}
	
	protected AbstractMatcher(final List<IMatcher<T>> children) {
		this.children = Preconditions.checkNotNull(children, "A list of 'children' must not be null.");
	}
	
	@Override
	public List<IMatcher<T>> getChildren() {
		return children;
	}
	
	
	private final List<IMatcher<T>> children;
	
}

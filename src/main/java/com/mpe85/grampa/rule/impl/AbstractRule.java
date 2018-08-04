package com.mpe85.grampa.rule.impl;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mpe85.grampa.rule.Rule;

import one.util.streamex.StreamEx;

public abstract class AbstractRule<T> implements Rule<T> {
	
	protected AbstractRule() {
		this(Collections.emptyList());
	}
	
	protected AbstractRule(final Rule<T> child) {
		this(Lists.newArrayList(child));
	}
	
	protected AbstractRule(final List<Rule<T>> children) {
		this.children = Preconditions.checkNotNull(children, "A list of 'children' must not be null.");
	}
	
	@Override
	public List<Rule<T>> getChildren() {
		return children;
	}
	
	@Override
	public Rule<T> getChild() {
		return children.size() > 0 ? children.get(0) : null;
	}
	
	@Override
	public boolean isPredicate() {
		return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(StreamEx.of(children)
				.mapToInt(System::identityHashCode)
				.toArray());
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final AbstractRule<?> other = (AbstractRule<?>) obj;
			return Objects.equals(this.children, other.children);
		}
		return false;
	}
	
	
	private final List<Rule<T>> children;
	
}

package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.collect.Lists;
import com.mpe85.grampa.intercept.RuleMethodInterceptor.ReferenceRule;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.visitor.RuleVisitor;

import one.util.streamex.StreamEx;

/**
 * An abstract rule that is base for all rule implementations.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public abstract class AbstractRule<T> implements Rule<T> {
	
	/**
	 * C'tor. Constructs an abstract rule with no child rules.
	 */
	protected AbstractRule() {
		this(Collections.emptyList());
	}
	
	/**
	 * C'tor. Constructs an abstract rule with one child rule.
	 * 
	 * @param child
	 *            a child rule
	 */
	protected AbstractRule(final Rule<T> child) {
		this(Lists.newArrayList(checkNotNull(child, "A 'child' must not be null.")));
	}
	
	/**
	 * C'tor. Constructs an abstract rule with multiple child rules.
	 * 
	 * @param children
	 *            a list of child rules
	 */
	protected AbstractRule(final List<Rule<T>> children) {
		this.children = checkNotNull(children, "A list of 'children' must not be null.");
	}
	
	@Override
	public List<Rule<T>> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	@Override
	public Rule<T> getChild() {
		return children.size() > 0 ? children.get(0) : null;
	}
	
	@Override
	public Rule<T> replaceReferenceRule(final int index, final Rule<T> replacementRule) {
		checkElementIndex(index, children.size(), "An 'index' must not be out of range.");
		checkNotNull(replacementRule, "A 'replacementRule' must not be null.");
		if (!(children.get(index) instanceof ReferenceRule)) {
			throw new IllegalArgumentException("Other rules than reference rules cannot be replaced.");
		}
		return children.set(index, replacementRule);
	}
	
	@Override
	public boolean isPredicate() {
		return false;
	}
	
	@Override
	public void accept(final RuleVisitor<T> visitor) {
		visitor.visit(this);
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
	
	protected ToStringHelper toStringHelper() {
		return MoreObjects.toStringHelper(this)
				.add("#children", children.size());
	}
	
	@Override
	public String toString() {
		return toStringHelper().toString();
	}
	
	
	private final List<Rule<T>> children;
	
}

package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.intercept.RuleMethodInterceptor;
import com.mpe85.grampa.rule.RuleContext;

/**
 * A reference rule implementation that stores a reference to a another rule. Note that this is only used by
 * {@link RuleMethodInterceptor}.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class ReferenceRule<T> extends AbstractRule<T> {
	
	public ReferenceRule(final int hashCode) {
		this.hashCode = hashCode;
	}
	
	@Override
	public boolean match(final RuleContext<T> context) {
		return false;
	}
	
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && getClass() == obj.getClass()) {
			final ReferenceRule<?> other = (ReferenceRule<?>) obj;
			return Objects.equals(hashCode, other.hashCode);
		}
		return false;
	}
	
	@Override
	protected ToStringHelper toStringHelper() {
		return super.toStringHelper()
				.add("hashCode", hashCode);
	}
	
	
	private final int hashCode;
	
}

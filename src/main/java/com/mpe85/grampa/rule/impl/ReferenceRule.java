package com.mpe85.grampa.rule.impl;

import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;
import com.mpe85.grampa.rule.RuleContext;

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

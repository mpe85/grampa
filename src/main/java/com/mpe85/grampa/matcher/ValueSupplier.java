package com.mpe85.grampa.matcher;

@FunctionalInterface
public interface ValueSupplier<T> {
	
	T supply(RuleContext<T> context);
	
}

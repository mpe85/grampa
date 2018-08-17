package com.mpe85.grampa.rule;

@FunctionalInterface
public interface EventSupplier<T> {
	
	Object supply(ActionContext<T> context);
	
}

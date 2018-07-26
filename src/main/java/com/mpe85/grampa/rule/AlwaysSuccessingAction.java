package com.mpe85.grampa.rule;

@FunctionalInterface
public interface AlwaysSuccessingAction<T> {
	
	void run(RuleContext<T> context);
	
}

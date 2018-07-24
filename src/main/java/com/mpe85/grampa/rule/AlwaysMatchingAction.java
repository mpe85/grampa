package com.mpe85.grampa.rule;

@FunctionalInterface
public interface AlwaysMatchingAction<T> {
	
	void run(RuleContext<T> context);
	
}

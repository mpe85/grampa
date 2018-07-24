package com.mpe85.grampa.matcher;

@FunctionalInterface
public interface AlwaysMatchingAction<T> {
	
	void run(RuleContext<T> context);
	
}

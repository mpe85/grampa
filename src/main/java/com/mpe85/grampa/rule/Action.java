package com.mpe85.grampa.rule;

@FunctionalInterface
public interface Action<T> {
	
	boolean run(RuleContext<T> context);
	
}

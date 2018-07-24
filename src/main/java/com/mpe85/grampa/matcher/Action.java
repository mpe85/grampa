package com.mpe85.grampa.matcher;

@FunctionalInterface
public interface Action<T> {
	
	boolean run(RuleContext<T> context);
	
}

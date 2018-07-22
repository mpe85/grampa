package com.mpe85.grampa.matcher;

@FunctionalInterface
public interface AlwaysMatchingAction<T> {
	
	void run(IMatcherContext<T> context);
	
}

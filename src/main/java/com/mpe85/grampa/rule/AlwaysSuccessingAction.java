package com.mpe85.grampa.rule;

@FunctionalInterface
public interface AlwaysSuccessingAction<T> {
	
	void run(ActionContext<T> context);
	
}

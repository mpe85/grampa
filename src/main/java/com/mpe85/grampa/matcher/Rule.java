package com.mpe85.grampa.matcher;

import java.util.List;

public interface Rule<T> {
	
	List<Rule<T>> getChildren();
	
	Rule<T> getChild();
	
	boolean match(RuleContext<T> context);
	
	boolean isPredicate();
	
}

package com.mpe85.grampa.matcher;

import java.util.List;

public interface IMatcher {
	
	List<IMatcher> getChildren();
	
	boolean isPredicate();
	
	<T> boolean match(IMatcherContext<T> context);
	
}

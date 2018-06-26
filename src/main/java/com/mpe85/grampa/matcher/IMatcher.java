package com.mpe85.grampa.matcher;

import java.util.List;

public interface IMatcher {
	
	List<IMatcher> getChildren();
	
	boolean isPredicate();
	
	boolean match(IMatcherContext context);
	
}

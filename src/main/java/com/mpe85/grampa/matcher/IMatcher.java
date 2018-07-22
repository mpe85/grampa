package com.mpe85.grampa.matcher;

import java.util.List;

public interface IMatcher<T> {
	
	List<IMatcher<T>> getChildren();
	
	IMatcher<T> getChild();
	
	boolean match(IMatcherContext<T> context);
	
}

package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputBuffer;

public interface RuleContext<T> extends ActionContext<T> {
	
	InputBuffer getInputBuffer();
	
	void setCurrentIndex(int currentIndex);
	
	boolean advanceIndex(int delta);
	
	RuleContext<T> getChildContext(Rule<T> rule);
	
	boolean run();
	
	RuleContext<T> getParent();
	
}

package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputBuffer;

public interface RuleContext<T> extends ActionContext<T> {
	
	InputBuffer getInputBuffer();
	
	void setCurrentIndex(int currentIndex);
	
	boolean advanceIndex(int delta);
	
	boolean run();
	
	@Override
	RuleContext<T> getParent();
	
	RuleContext<T> createChildContext(Rule<T> rule);
	
}

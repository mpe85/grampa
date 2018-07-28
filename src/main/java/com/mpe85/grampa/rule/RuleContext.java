package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.util.stack.RestorableStack;

public interface RuleContext<T> {
	
	InputBuffer getInputBuffer();
	
	int getCurrentIndex();
	
	void setCurrentIndex(int currentIndex);
	
	int getStartIndex();
	
	boolean isAtEndOfInput();
	
	char getCurrentChar();
	
	int getCurrentCodePoint();
	
	int getNumberOfCharsLeft();
	
	int getLevel();
	
	boolean advanceIndex(int delta);
	
	RuleContext<T> getChildContext(Rule<T> rule);
	
	boolean run();
	
	RestorableStack<T> getValueStack();
	
	boolean inPredicate();
	
	CharSequence getPreviousMatch();
	
	InputPosition getPosition();
	
	CharSequence getRestOfInput();
	
	RuleContext<T> getParent();
	
}

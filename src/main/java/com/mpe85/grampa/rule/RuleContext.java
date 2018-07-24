package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.util.stack.IRestorableStack;

public interface RuleContext<T> {
	
	IInputBuffer getInputBuffer();
	
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
	
	IRestorableStack<T> getValueStack();
	
	boolean inPredicate();
	
	String getMatch();
	
	InputPosition getPosition();
	
}

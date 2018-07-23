package com.mpe85.grampa.matcher;

import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.util.stack.IRestorableStack;

public interface IMatcherContext<T> {
	
	IInputBuffer getInputBuffer();
	
	int getCurrentIndex();
	
	void setCurrentIndex(int currentIndex);
	
	boolean isAtEndOfInput();
	
	char getCurrentChar();
	
	int getCurrentCodePoint();
	
	int getNumberOfCharsLeft();
	
	int getLevel();
	
	boolean advanceIndex(int delta);
	
	IMatcherContext<T> getChildContext(IMatcher<T> matcher);
	
	boolean run();
	
	IRestorableStack<T> getValueStack();
	
	boolean inPredicate();
	
}

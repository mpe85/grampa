package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.util.stack.RestorableStack;

public interface ActionContext<T> {
	
	int getCurrentIndex();
	
	int getStartIndex();
	
	boolean isAtEndOfInput();
	
	char getCurrentChar();
	
	int getCurrentCodePoint();
	
	int getNumberOfCharsLeft();
	
	int getLevel();
	
	boolean inPredicate();
	
	CharSequence getPreviousMatch();
	
	InputPosition getPosition();
	
	CharSequence getRestOfInput();
	
	RestorableStack<T> getValueStack();
	
}

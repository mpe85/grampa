package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.util.stack.RestorableStack;

public interface ActionContext<T> {
	
	int getLevel();
	
	int getStartIndex();
	
	int getCurrentIndex();
	
	boolean isAtEndOfInput();
	
	char getCurrentChar();
	
	int getCurrentCodePoint();
	
	int getNumberOfCharsLeft();
	
	CharSequence getMatchedInput();
	
	CharSequence getRestOfInput();
	
	CharSequence getPreviousMatch();
	
	InputPosition getPosition();
	
	boolean inPredicate();
	
	RestorableStack<T> getStack();
	
	ActionContext<T> getParent();
	
}

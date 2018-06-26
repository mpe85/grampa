package com.mpe85.grampa.matcher;

import com.mpe85.grampa.input.IInputBuffer;

public interface IMatcherContext {
	
	IInputBuffer getInputBuffer();
	
	int getCurrentIndex();
	
	boolean isAtEndOfInput();
	
	char getCurrentChar();
	
	int getCurrentCodePoint();
	
	int getLevel();
	
	void advanceIndex(int delta);
	
	IMatcherContext getChildContext(IMatcher matcher);
	
	boolean run(IMatcher matcher);
	
}

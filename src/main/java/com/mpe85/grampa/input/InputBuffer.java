package com.mpe85.grampa.input;

public interface InputBuffer {
	
	char getChar(int index);
	
	int getCodePoint(int index);
	
	int getLength();
	
	CharSequence subSequence(int startIndex, int endIndex);
	
	InputPosition getPosition(int index);
	
}

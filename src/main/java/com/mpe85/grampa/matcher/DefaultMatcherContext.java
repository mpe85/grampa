package com.mpe85.grampa.matcher;

import com.mpe85.grampa.input.IInputBuffer;

public class DefaultMatcherContext implements IMatcherContext {
	
	public DefaultMatcherContext(
			final IInputBuffer inputBuffer,
			final int level) {
		this.inputBuffer = inputBuffer;
		this.level = level;
	}
	
	@Override
	public IInputBuffer getInputBuffer() {
		return inputBuffer;
	}
	
	@Override
	public int getCurrentIndex() {
		return currentIndex;
	}
	
	@Override
	public boolean isAtEndOfInput() {
		return currentIndex == inputBuffer.getLength();
	}
	
	@Override
	public char getCurrentChar() {
		return inputBuffer.getChar(currentIndex);
	}
	
	@Override
	public int getCurrentCodePoint() {
		return inputBuffer.getCodePoint(currentIndex);
	}
	
	@Override
	public int getLevel() {
		return level;
	}
	
	
	private final IInputBuffer inputBuffer;
	private final int level;
	
	private int currentIndex;
	
}

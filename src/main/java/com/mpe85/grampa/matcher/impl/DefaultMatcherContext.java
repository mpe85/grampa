package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;

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
	public int getNumberOfCharsLeft() {
		return inputBuffer.getLength() - currentIndex;
	}
	
	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public boolean advanceIndex(final int delta) {
		Preconditions.checkArgument(delta >= 0, "A 'delta' must be greater or equal 0.");
		if (currentIndex + delta <= inputBuffer.getLength()) {
			currentIndex += delta;
			return true;
		}
		return false;
	}
	
	@Override
	public IMatcherContext getChildContext(final IMatcher matcher) {
		return childContext;
	}
	
	@Override
	public boolean run(final IMatcher matcher) {
		final boolean matched = matcher.match(this);
		if (matched && parentContext != null) {
			parentContext.currentIndex = currentIndex;
		}
		return matched;
	}
	
	
	private final IInputBuffer inputBuffer;
	private final int level;
	
	private int currentIndex;
	private DefaultMatcherContext parentContext;
	private DefaultMatcherContext childContext;
	
}

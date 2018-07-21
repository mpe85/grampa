package com.mpe85.grampa.matcher.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;
import com.mpe85.grampa.util.stack.IRestorableStack;

public class DefaultMatcherContext<T> implements IMatcherContext<T> {
	
	public DefaultMatcherContext(
			final IInputBuffer inputBuffer,
			final int level,
			final IMatcher<T> matcher,
			final int startIndex,
			final IRestorableStack<T> valueStack) {
		this.inputBuffer = inputBuffer;
		this.level = level;
		this.matcher = matcher;
		this.startIndex = startIndex;
		this.currentIndex = startIndex;
		this.valueStack = valueStack;
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
	public IMatcherContext<T> getChildContext(final IMatcher<T> matcher) {
		return new DefaultMatcherContext<>(inputBuffer, level + 1, matcher, currentIndex, valueStack);
	}
	
	@Override
	public boolean run() {
		final boolean matched = matcher.match(this);
		if (matched && parentContext != null) {
			parentContext.currentIndex = currentIndex;
		}
		return matched;
	}
	
	@Override
	public IRestorableStack<T> getValueStack() {
		return valueStack;
	}
	
	
	private final IInputBuffer inputBuffer;
	private final int level;
	private final IMatcher<T> matcher;
	private final int startIndex;
	private final IRestorableStack<T> valueStack;
	
	private int currentIndex;
	private DefaultMatcherContext<T> parentContext;
	
}

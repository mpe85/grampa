package com.mpe85.grampa.matcher.impl;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.input.InputPosition;
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
		this(inputBuffer, level, matcher, startIndex, valueStack, null);
	}
	
	public DefaultMatcherContext(
			final IInputBuffer inputBuffer,
			final int level,
			final IMatcher<T> matcher,
			final int startIndex,
			final IRestorableStack<T> valueStack,
			final IMatcherContext<T> parentContext) {
		this.inputBuffer = Preconditions.checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		this.level = level;
		this.matcher = Preconditions.checkNotNull(matcher, "A 'matcher' must not be null.");
		this.startIndex = startIndex;
		this.currentIndex = startIndex;
		this.valueStack = Preconditions.checkNotNull(valueStack, "A 'valueStack' must not be null.");
		this.parentContext = parentContext;
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
	public void setCurrentIndex(final int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	@Override
	public int getStartIndex() {
		return startIndex;
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
		return new DefaultMatcherContext<>(inputBuffer, level + 1, matcher, currentIndex, valueStack, this);
	}
	
	@Override
	public boolean run() {
		final boolean matched = matcher.match(this);
		if (matched && parentContext != null) {
			parentContext.setCurrentIndex(currentIndex);
		}
		return matched;
	}
	
	@Override
	public IRestorableStack<T> getValueStack() {
		return valueStack;
	}
	
	@Override
	public boolean inPredicate() {
		return matcher.isPredicate()
				|| Optional.ofNullable(parentContext).map(IMatcherContext::inPredicate).orElse(false);
	}
	
	@Override
	public String getMatch() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public InputPosition getPosition() {
		return inputBuffer.getPosition(currentIndex);
	}
	
	
	private final IInputBuffer inputBuffer;
	private final int level;
	private final IMatcher<T> matcher;
	private final int startIndex;
	private final IRestorableStack<T> valueStack;
	
	private int currentIndex;
	private IMatcherContext<T> parentContext;
	
}

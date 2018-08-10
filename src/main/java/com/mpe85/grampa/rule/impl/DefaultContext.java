package com.mpe85.grampa.rule.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import java.util.Optional;

import com.google.common.eventbus.EventBus;
import com.mpe85.grampa.event.MatchFailureEvent;
import com.mpe85.grampa.event.MatchSuccessEvent;
import com.mpe85.grampa.event.PreMatchEvent;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.rule.ActionContext;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;

public class DefaultContext<T> implements RuleContext<T>, ActionContext<T> {
	
	public DefaultContext(
			final InputBuffer inputBuffer,
			final int level,
			final Rule<T> rule,
			final int startIndex,
			final RestorableStack<T> stack,
			final EventBus bus) {
		this(inputBuffer, level, rule, startIndex, stack, bus, null);
	}
	
	public DefaultContext(
			final InputBuffer inputBuffer,
			final int level,
			final Rule<T> rule,
			final int startIndex,
			final RestorableStack<T> stack,
			final EventBus bus,
			final RuleContext<T> parentContext) {
		this.inputBuffer = checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		this.level = level;
		this.rule = checkNotNull(rule, "A 'rule' must not be null.");
		this.startIndex = startIndex;
		this.currentIndex = startIndex;
		this.stack = checkNotNull(stack, "A 'stack' must not be null.");
		this.bus = checkNotNull(bus, "A 'bus' must not be null.");
		this.parentContext = parentContext;
		this.previousMatch = Optional.ofNullable(parentContext).map(RuleContext::getPreviousMatch).orElse(null);
	}
	
	@Override
	public int getLevel() {
		return level;
	}
	
	@Override
	public int getStartIndex() {
		return startIndex;
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
		if (cachedCurrentChar == null) {
			cachedCurrentChar = inputBuffer.getChar(currentIndex);
		}
		return cachedCurrentChar;
	}
	
	@Override
	public int getCurrentCodePoint() {
		if (cachedCurrentCodePoint == null) {
			cachedCurrentCodePoint = inputBuffer.getCodePoint(currentIndex);
		}
		return cachedCurrentCodePoint;
	}
	
	@Override
	public int getNumberOfCharsLeft() {
		return inputBuffer.getLength() - currentIndex;
	}
	
	@Override
	public CharSequence getRestOfInput() {
		return inputBuffer.subSequence(currentIndex, inputBuffer.getLength());
	}
	
	@Override
	public CharSequence getPreviousMatch() {
		return previousMatch;
	}
	
	@Override
	public InputPosition getPosition() {
		return inputBuffer.getPosition(currentIndex);
	}
	
	@Override
	public boolean inPredicate() {
		return rule.isPredicate()
				|| Optional.ofNullable(parentContext).map(RuleContext::inPredicate).orElse(false);
	}
	
	@Override
	public RestorableStack<T> getStack() {
		return stack;
	}
	
	@Override
	public RuleContext<T> getParent() {
		return parentContext;
	}
	
	@Override
	public InputBuffer getInputBuffer() {
		return inputBuffer;
	}
	
	@Override
	public void setCurrentIndex(final int currentIndex) {
		checkPositionIndex(currentIndex, inputBuffer.getLength(), "A 'currentIndex' must be in range.");
		if (currentIndex > this.currentIndex) {
			previousMatch = inputBuffer.subSequence(this.currentIndex, currentIndex);
		}
		invalidateCache();
		this.currentIndex = currentIndex;
	}
	
	@Override
	public boolean advanceIndex(final int delta) {
		checkArgument(delta >= 0, "A 'delta' must be greater or equal 0.");
		if (currentIndex + delta <= inputBuffer.getLength()) {
			currentIndex += delta;
			invalidateCache();
			return true;
		}
		return false;
	}
	
	@Override
	public boolean run() {
		stack.takeSnapshot();
		bus.post(new PreMatchEvent<>(this));
		final boolean matched = rule.match(this);
		if (matched && parentContext != null) {
			parentContext.setCurrentIndex(currentIndex);
		}
		if (matched) {
			bus.post(new MatchSuccessEvent<>(this));
		}
		else {
			bus.post(new MatchFailureEvent<>(this));
		}
		stack.removeSnapshot(!matched);
		return matched;
	}
	
	@Override
	public RuleContext<T> createChildContext(final Rule<T> rule) {
		return new DefaultContext<>(inputBuffer, level + 1, rule, currentIndex, stack, bus, this);
	}
	
	private void invalidateCache() {
		cachedCurrentChar = null;
		cachedCurrentCodePoint = null;
	}
	
	
	private final InputBuffer inputBuffer;
	private final int level;
	private final Rule<T> rule;
	private final int startIndex;
	private final RestorableStack<T> stack;
	private final EventBus bus;
	
	private int currentIndex;
	private Character cachedCurrentChar;
	private Integer cachedCurrentCodePoint;
	private CharSequence previousMatch;
	private RuleContext<T> parentContext;
	
}

package com.mpe85.grampa.rule.impl;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;

public class DefaultRuleContext<T> implements RuleContext<T> {
	
	public DefaultRuleContext(
			final InputBuffer inputBuffer,
			final int level,
			final Rule<T> rule,
			final int startIndex,
			final RestorableStack<T> valueStack) {
		this(inputBuffer, level, rule, startIndex, valueStack, null);
	}
	
	public DefaultRuleContext(
			final InputBuffer inputBuffer,
			final int level,
			final Rule<T> rule,
			final int startIndex,
			final RestorableStack<T> valueStack,
			final RuleContext<T> parentContext) {
		this.inputBuffer = Preconditions.checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		this.level = level;
		this.rule = Preconditions.checkNotNull(rule, "A 'rule' must not be null.");
		this.startIndex = startIndex;
		this.currentIndex = startIndex;
		this.valueStack = Preconditions.checkNotNull(valueStack, "A 'valueStack' must not be null.");
		this.parentContext = parentContext;
	}
	
	@Override
	public InputBuffer getInputBuffer() {
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
	public RuleContext<T> getChildContext(final Rule<T> rule) {
		return new DefaultRuleContext<>(inputBuffer, level + 1, rule, currentIndex, valueStack, this);
	}
	
	@Override
	public boolean run() {
		final boolean matched = rule.match(this);
		if (matched && parentContext != null) {
			parentContext.setCurrentIndex(currentIndex);
		}
		return matched;
	}
	
	@Override
	public RestorableStack<T> getValueStack() {
		return valueStack;
	}
	
	@Override
	public boolean inPredicate() {
		return rule.isPredicate()
				|| Optional.ofNullable(parentContext).map(RuleContext::inPredicate).orElse(false);
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
	
	
	private final InputBuffer inputBuffer;
	private final int level;
	private final Rule<T> rule;
	private final int startIndex;
	private final RestorableStack<T> valueStack;
	
	private int currentIndex;
	private RuleContext<T> parentContext;
	
}

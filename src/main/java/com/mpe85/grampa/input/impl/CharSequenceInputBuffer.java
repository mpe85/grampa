package com.mpe85.grampa.input.impl;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.InputPosition;

import one.util.streamex.IntStreamEx;

public class CharSequenceInputBuffer implements InputBuffer {
	
	public CharSequenceInputBuffer(final CharSequence charSequence) {
		Preconditions.checkNotNull(charSequence, "A 'charSequence' must not be null.");
		this.charSequence = charSequence;
		lineCounter = new CharSequenceLineCounter(charSequence);
	}
	
	@Override
	public char getChar(final int index) {
		Preconditions.checkElementIndex(index, getLength(), "An 'index' must not be out of range.");
		return charSequence.charAt(index);
	}
	
	@Override
	public int getCodePoint(final int index) {
		return IntStreamEx.of(charSequence.codePoints()).toArray()[index];
	}
	
	@Override
	public int getLength() {
		return charSequence.length();
	}
	
	@Override
	public CharSequence subSequence(final int startIndex, final int endIndex) {
		Preconditions.checkPositionIndex(startIndex, getLength(), "A 'startIndex' must not be out of range.");
		Preconditions.checkPositionIndex(endIndex, getLength(), "An 'endIndex' must not be out of range.");
		Preconditions.checkPositionIndexes(startIndex, endIndex, getLength());
		return charSequence.subSequence(startIndex, endIndex);
	}
	
	@Override
	public InputPosition getPosition(final int index) {
		Preconditions.checkElementIndex(index, getLength(), "A 'startIndex' must not be out of range.");
		return lineCounter.getPosition(index);
	}
	
	
	private final CharSequence charSequence;
	private final CharSequenceLineCounter lineCounter;
	
}

package com.mpe85.grampa.input.impl;

import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;
import static com.google.common.base.Preconditions.checkPositionIndexes;

import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.InputPosition;

import one.util.streamex.IntStreamEx;

/**
 * An {@link InputBuffer} implementation using a {@link CharSequence}.
 * 
 * @author mpe85
 *
 */
public class CharSequenceInputBuffer implements InputBuffer {
	
	private final CharSequence charSequence;
	private final CharSequenceLineCounter lineCounter;
	
	public CharSequenceInputBuffer(final CharSequence charSequence) {
		this.charSequence = checkNotNull(charSequence, "A 'charSequence' must not be null.");
		lineCounter = new CharSequenceLineCounter(charSequence);
	}
	
	@Override
	public char getChar(final int index) {
		return charSequence.charAt(
				checkElementIndex(index, getLength(), "An 'index' must not be out of range."));
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
		checkPositionIndex(startIndex, getLength(), "A 'startIndex' must not be out of range.");
		checkPositionIndex(endIndex, getLength(), "An 'endIndex' must not be out of range.");
		checkPositionIndexes(startIndex, endIndex, getLength());
		return charSequence.subSequence(startIndex, endIndex);
	}
	
	@Override
	public InputPosition getPosition(final int index) {
		return lineCounter.getPosition(
				checkElementIndex(index, getLength(), "A 'startIndex' must not be out of range."));
	}
	
}

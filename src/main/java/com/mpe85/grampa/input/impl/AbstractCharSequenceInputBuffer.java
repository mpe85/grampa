package com.mpe85.grampa.input.impl;

import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.input.InputPosition;

public abstract class AbstractCharSequenceInputBuffer implements IInputBuffer {
	
	public AbstractCharSequenceInputBuffer(final CharSequence charSequence) {
		this.charSequence = charSequence;
		lineCounter = new CharSequenceLineCounter(charSequence);
	}
	
	@Override
	public char getChar(final int index) {
		return charSequence.charAt(index);
	}
	
	@Override
	public int getLength() {
		return charSequence.length();
	}
	
	@Override
	public CharSequence subSequence(final int startIndex, final int endIndex) {
		final int rangedStart = Math.min(Math.max(startIndex, 0), getLength());
		final int rangedEnd = Math.min(Math.max(endIndex, rangedStart), getLength());
		return charSequence.subSequence(rangedStart, rangedEnd);
	}
	
	@Override
	public InputPosition getPosition(final int index) {
		return lineCounter.getPosition(index);
	}
	
	
	private final CharSequence charSequence;
	private final CharSequenceLineCounter lineCounter;
	
}

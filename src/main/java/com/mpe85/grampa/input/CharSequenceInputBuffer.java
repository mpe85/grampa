package com.mpe85.grampa.input;

public abstract class CharSequenceInputBuffer implements InputBuffer {
	
	public CharSequenceInputBuffer(final CharSequence charSequence) {
		this.charSequence = charSequence;
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
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private final CharSequence charSequence;
	
}

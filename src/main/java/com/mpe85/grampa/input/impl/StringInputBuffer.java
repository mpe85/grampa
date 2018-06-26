package com.mpe85.grampa.input.impl;

public class StringInputBuffer extends AbstractCharSequenceInputBuffer {
	
	public StringInputBuffer(final String string) {
		super(string);
		this.string = string;
	}
	
	@Override
	public int getCodePoint(final int index) {
		return string.codePointAt(index);
	}
	
	
	private final String string;
	
}

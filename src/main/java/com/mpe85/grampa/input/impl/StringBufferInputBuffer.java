package com.mpe85.grampa.input.impl;

public class StringBufferInputBuffer extends CharSequenceInputBuffer {
	
	public StringBufferInputBuffer(final StringBuffer stringBuffer) {
		super(stringBuffer);
		this.stringBuffer = stringBuffer;
	}
	
	@Override
	public int getCodePoint(final int index) {
		return stringBuffer.codePointAt(index);
	}
	
	
	private final StringBuffer stringBuffer;
	
}

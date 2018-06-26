package com.mpe85.grampa.input;

public class StringBufferInputBuffer extends AbstractCharSequenceInputBuffer {
	
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

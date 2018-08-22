package com.mpe85.grampa.input.impl;

import com.mpe85.grampa.input.InputBuffer;

/**
 * An {@link InputBuffer} implementation using a {@link StringBuffer}.
 * 
 * @author mpe85
 *
 */
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

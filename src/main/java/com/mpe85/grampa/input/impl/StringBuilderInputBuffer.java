package com.mpe85.grampa.input.impl;

import com.mpe85.grampa.input.InputBuffer;

/**
 * An {@link InputBuffer} implementation using a {@link StringBuilder}.
 * 
 * @author mpe85
 *
 */
public class StringBuilderInputBuffer extends CharSequenceInputBuffer {
	
	private final StringBuilder stringBuilder;
	
	public StringBuilderInputBuffer(final StringBuilder stringBuilder) {
		super(stringBuilder);
		this.stringBuilder = stringBuilder;
	}
	
	@Override
	public int getCodePoint(final int index) {
		return stringBuilder.codePointAt(index);
	}
	
}

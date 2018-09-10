package com.mpe85.grampa.input.impl;

import com.mpe85.grampa.input.InputBuffer;

/**
 * An {@link InputBuffer} implementation using a {@link String}.
 * 
 * @author mpe85
 *
 */
public class StringInputBuffer extends CharSequenceInputBuffer {
	
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

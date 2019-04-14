package com.mpe85.grampa.input.impl;

import com.ibm.icu.impl.StringSegment;
import com.mpe85.grampa.input.InputBuffer;

/**
 * An {@link InputBuffer} implementation using a {@link StringSegment}.
 * 
 * @author mpe85
 *
 */
public class StringSegmentInputBuffer extends CharSequenceInputBuffer {
	
	private final StringSegment stringSegment;
	
	public StringSegmentInputBuffer(final StringSegment stringSegment) {
		super(stringSegment);
		this.stringSegment = stringSegment;
	}
	
	@Override
	public int getCodePoint(final int index) {
		return stringSegment.codePointAt(index);
	}
	
}

package com.mpe85.grampa.input.impl;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.input.LineCounter;

/**
 * A {@link LineCounter} implementation for {@link CharSequence}s.
 * 
 * @author mpe85
 *
 */
public class CharSequenceLineCounter implements LineCounter {
	
	public CharSequenceLineCounter(final CharSequence input) {
		Preconditions.checkNotNull(input, "An 'input' must not be null.");
		length = input.length();
		initLines(input);
	}
	
	private void initLines(final CharSequence input) {
		for (int currentIdx = 0, lineStartIdx = 0, lineNumber = 0; currentIdx < length; currentIdx++) {
			if (input.charAt(currentIdx) == LF) {
				lines.put(lineStartIdx, ++lineNumber);
				lineStartIdx = currentIdx + 1;
			}
			else if (currentIdx == length - 1) {
				lines.put(lineStartIdx, ++lineNumber);
			}
		}
	}
	
	@Override
	public int getLineCount() {
		return lines.size();
	}
	
	@Override
	public InputPosition getPosition(final int index) {
		Preconditions.checkElementIndex(index, length, "An 'index' must not be out of range.");
		return Optional.of(index)
				.map(lines::floorEntry)
				.map(e -> new InputPosition(e.getValue(), index - e.getKey() + 1))
				.get();
	}
	
	
	private static final char LF = '\n';
	
	private final NavigableMap<Integer, Integer> lines = new TreeMap<>();
	private final int length;
	
}

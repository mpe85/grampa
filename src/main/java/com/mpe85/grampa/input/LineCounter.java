package com.mpe85.grampa.input;

import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

public class LineCounter {
	
	public LineCounter(final CharSequence input) {
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
	
	public int getLineCount() {
		return lines.size();
	}
	
	public InputPosition getPosition(final int index) {
		return Optional.of(index)
				.map(lines::floorEntry)
				.map(e -> new InputPosition(e.getValue(), index - e.getKey() + 1))
				.get();
	}
	
	
	private static final char LF = '\n';
	
	private final NavigableMap<Integer, Integer> lines = new TreeMap<>();
	private final int length;
	
}

package com.mpe85.grampa.input;

/**
 * Defines a position inside a text input (line and column)
 * 
 * @author mpe85
 *
 */
public class InputPosition {
	
	private final int line;
	private final int column;
	
	public InputPosition(
			final int line,
			final int column) {
		this.line = line;
		this.column = column;
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
}

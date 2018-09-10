package com.mpe85.grampa.input;

/**
 * A counter for lines in texts.
 * 
 * @author mpe85
 *
 */
public interface LineCounter {
	
	/**
	 * Gets the line count.
	 * 
	 * @return the line count
	 */
	int getLineCount();
	
	/**
	 * Gets the position of a character at a given index.
	 * 
	 * @param index
	 *            a valid index
	 * @return the input position
	 */
	InputPosition getPosition(int index);
	
}

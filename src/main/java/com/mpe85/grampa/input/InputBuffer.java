package com.mpe85.grampa.input;

/**
 * An input buffer that buffers the parser input.
 * 
 * @author mpe85
 *
 */
public interface InputBuffer {
	
	/**
	 * Gets the character at a given index.
	 * 
	 * @param index
	 *        a valid index inside the input
	 * @return a character
	 */
	char getChar(int index);
	
	/**
	 * Gets the code point at a given index.
	 * 
	 * @param index
	 *        a valid index inside the input
	 * @return a code point
	 */
	int getCodePoint(int index);
	
	/**
	 * Gets the length of the input.
	 * 
	 * @return the length
	 */
	int getLength();
	
	/**
	 * Gets a sub sequence of the input.
	 * 
	 * @param startIndex
	 *        a valid start index
	 * @param endIndex
	 *        a valid end index
	 * @return the sub sequence
	 */
	CharSequence subSequence(int startIndex, int endIndex);
	
	/**
	 * Gets the position (line and column) of a character at a given index inside the input.
	 * 
	 * @param index
	 *        a valid index
	 * @return a position
	 */
	InputPosition getPosition(int index);
	
}

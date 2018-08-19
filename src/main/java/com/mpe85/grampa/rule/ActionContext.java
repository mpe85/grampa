package com.mpe85.grampa.rule;

import com.mpe85.grampa.input.InputPosition;
import com.mpe85.grampa.util.stack.RestorableStack;

/**
 * Defines a context for parser action rules.
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
public interface ActionContext<T> {
	
	/**
	 * Gets the current level (depth) inside the context parent/child hierarchy.
	 * 
	 * @return a level.
	 */
	int getLevel();
	
	/**
	 * Gets the start index inside the parser input. This is the index where the context tries to match its rule.
	 * 
	 * @return the start index
	 */
	int getStartIndex();
	
	/**
	 * Gets the current index inside the parser input.
	 * 
	 * @return the current index
	 */
	int getCurrentIndex();
	
	/**
	 * Checks if the current index is at the end of the input.
	 * 
	 * @return true if the index is at the end, otherwise false
	 */
	boolean isAtEndOfInput();
	
	/**
	 * Gets the character at the current index.
	 * 
	 * @return a character
	 */
	char getCurrentChar();
	
	/**
	 * Gets the code point at the current index.
	 * 
	 * @return a code point
	 */
	int getCurrentCodePoint();
	
	/**
	 * Gets the number of characters left in the input.
	 * 
	 * @return the number characters
	 */
	int getNumberOfCharsLeft();
	
	/**
	 * Gets the whole parser input.
	 * 
	 * @return a character sequence
	 */
	CharSequence getInput();
	
	/**
	 * Gets the input that is already matched successfully.
	 * 
	 * @return a character sequence
	 */
	CharSequence getMatchedInput();
	
	/**
	 * Gets the rest of the input that is not matched yet.
	 * 
	 * @return a character sequence
	 */
	CharSequence getRestOfInput();
	
	/**
	 * Gets the part of the input that was matched by the previous rule.
	 * 
	 * @return a character sequence
	 */
	CharSequence getPreviousMatch();
	
	/**
	 * Gets the position of the current index.
	 * 
	 * @return the current position
	 */
	InputPosition getPosition();
	
	/**
	 * Checks if the context is run inside a predicate rule.
	 * 
	 * @return true if the context is inside a predicate rule, otherwise false
	 */
	boolean inPredicate();
	
	/**
	 * Gets the parser stack.
	 * 
	 * @return a restorable stack
	 */
	RestorableStack<T> getStack();
	
	/**
	 * Gets the parent context of the context
	 * 
	 * @return the parent action context, or null for the root context
	 */
	ActionContext<T> getParent();
	
	/**
	 * Posts a parser event to the event bus.
	 * 
	 * @param event
	 *        an event to post
	 */
	void post(Object event);
	
}

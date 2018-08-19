package com.mpe85.grampa.parser;

import com.mpe85.grampa.rule.Rule;

/**
 * Defines a parser.
 * 
 * @author mpe85
 *
 * @param <T>
 *        the type of the stack elements
 */
public interface Parser<T> {
	
	/**
	 * Gets the root rule of the parser.
	 * 
	 * @return a parser rule
	 */
	Rule<T> root();
	
}

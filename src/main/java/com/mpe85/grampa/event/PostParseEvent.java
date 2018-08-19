package com.mpe85.grampa.event;

import com.mpe85.grampa.runner.ParseResult;

/**
 * Event posted after a parse run.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class PostParseEvent<T> {
	
	public PostParseEvent(final ParseResult<T> result) {
		this.result = result;
	}
	
	public ParseResult<T> getResult() {
		return result;
	}
	
	private final ParseResult<T> result;
	
}

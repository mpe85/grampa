package com.mpe85.grampa.event;

import com.mpe85.grampa.runner.ParseResult;

public class PostParseEvent<T> {
	
	public PostParseEvent(final ParseResult<T> result) {
		this.result = result;
	}
	
	public ParseResult<T> getResult() {
		return result;
	}
	
	private final ParseResult<T> result;
	
}

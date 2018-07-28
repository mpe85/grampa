package com.mpe85.grampa.event;

import com.google.common.eventbus.Subscribe;

public abstract class ParseEventListener<T> {
	
	@Subscribe
	public void beforeParse(final PreParseEvent<T> event) {
	}
	
	@Subscribe
	public void beforeMatch(final PreMatchEvent<T> event) {
	}
	
	@Subscribe
	public void afterMatchSuccess(final MatchSuccessEvent<T> event) {
	}
	
	@Subscribe
	public void afterMatchFailure(final MatchFailureEvent<T> event) {
	}
	
	@Subscribe
	public void afterParse(final PostParseEvent<T> event) {
	}
	
}

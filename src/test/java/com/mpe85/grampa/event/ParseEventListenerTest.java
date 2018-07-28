package com.mpe85.grampa.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.eventbus.EventBus;

public class ParseEventListenerTest {
	
	@Test
	public void postEvent_valid() {
		class TestListener extends ParseEventListener<String> {
			@Override
			public void beforeParse(final PreParseEvent<String> event) {
				called = true;
			}
			
			boolean called = false;
		}
		
		final TestListener listener = new TestListener();
		
		final EventBus bus = new EventBus();
		bus.register(listener);
		bus.post(new PreParseEvent<>(null));
		
		assertTrue(listener.called);
	}
	
	@Test
	public void postEvent_exception() {
		class TestListener extends ParseEventListener<String> {
			@Override
			public void beforeParse(final PreParseEvent<String> event) {
				throw new RuntimeException("failure");
			}
		}
		
		final TestListener listener = new TestListener();
		
		final StringBuilder sb = new StringBuilder();
		
		final EventBus bus = new EventBus((ex, ctx) -> sb.append(ex.getMessage()));
		bus.register(listener);
		bus.post(new PreParseEvent<>(null));
		
		assertEquals("failure", sb.toString());
	}
	
}

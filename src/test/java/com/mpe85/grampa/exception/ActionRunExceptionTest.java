package com.mpe85.grampa.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ActionRunExceptionTest {
	
	@Test
	public void create() {
		final RuntimeException cause = new RuntimeException();
		
		final ActionRunException ex1 = new ActionRunException("someMessage");
		final ActionRunException ex2 = new ActionRunException(cause);
		final ActionRunException ex3 = new ActionRunException("someMessage", cause);
		
		assertEquals("someMessage", ex1.getMessage());
		assertNull(ex1.getCause());
		
		assertEquals(cause.toString(), ex2.getMessage());
		assertEquals(cause, ex2.getCause());
		
		assertEquals("someMessage", ex3.getMessage());
		assertEquals(cause, ex3.getCause());
	}
	
}

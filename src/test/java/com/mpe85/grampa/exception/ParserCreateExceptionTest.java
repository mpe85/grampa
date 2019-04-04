package com.mpe85.grampa.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ParserCreateExceptionTest {
	
	@Test
	public void create() {
		final RuntimeException cause = new RuntimeException();
		
		final ParserCreateException ex1 = new ParserCreateException("someMessage");
		final ParserCreateException ex2 = new ParserCreateException(cause);
		final ParserCreateException ex3 = new ParserCreateException("someMessage", cause);
		
		assertEquals("someMessage", ex1.getMessage());
		assertNull(ex1.getCause());
		
		assertEquals(cause.toString(), ex2.getMessage());
		assertEquals(cause, ex2.getCause());
		
		assertEquals("someMessage", ex3.getMessage());
		assertEquals(cause, ex3.getCause());
	}
	
}

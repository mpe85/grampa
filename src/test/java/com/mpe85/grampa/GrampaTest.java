package com.mpe85.grampa;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.Rule;

public class GrampaTest {
	
	@Test
	public void testCreateParser() {
		final TestParser p = Grampa.createParser(TestParser.class);
		final Rule<String> root = p.root();
		// TODO test...
	}
	
	@Test
	public void testCreateParser2() {
		final TestParser p = Grampa.createParser(TestParser.class, String.class).withArgs("foo");
		final Rule<String> root = p.root();
		// TODO test...
	}
}

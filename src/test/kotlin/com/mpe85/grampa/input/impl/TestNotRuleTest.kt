package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TestNotRuleTest {
	
	@Test
	public void isPredicate() {
		assertTrue(new TestNotRule<>(new EmptyRule<>()).isPredicate());
	}
	
}

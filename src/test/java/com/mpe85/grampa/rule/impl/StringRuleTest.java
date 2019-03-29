package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		final StringRule<String> rule1 = new StringRule<>("string");
		final StringRule<String> rule2 = new StringRule<>("string", false);
		final StringRule<String> rule3 = new StringRule<>("string", true);
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals("StringRule{#children=0, string=string, ignoreCase=false}", rule1.toString());
		assertEquals("StringRule{#children=0, string=string, ignoreCase=false}", rule2.toString());
		assertEquals("StringRule{#children=0, string=string, ignoreCase=true}", rule3.toString());
	}
	
}

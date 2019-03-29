package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.Rule;

public class RepeatRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		final Rule<String> empty = new EmptyRule<>();
		
		final RepeatRule<String> rule1 = new RepeatRule<>(empty, 0, 5);
		final RepeatRule<String> rule2 = new RepeatRule<>(new EmptyRule<>(), 0, 5);
		final RepeatRule<String> rule3 = new RepeatRule<>(empty, 2, null);
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals("RepeatRule{#children=1, min=0, max=5}", rule1.toString());
		assertEquals("RepeatRule{#children=1, min=0, max=5}", rule2.toString());
		assertEquals("RepeatRule{#children=1, min=2, max=null}", rule3.toString());
	}
	
}

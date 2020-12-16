package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.Action;

public class ActionRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		final Action<String> action = ctx -> true;
		
		final ActionRule<String> rule1 = new ActionRule<>(action);
		final ActionRule<String> rule2 = new ActionRule<>(action, false);
		final ActionRule<String> rule3 = new ActionRule<>(ctx -> true);
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals("ActionRule{#children=0, skippable=false}", rule1.toString());
		assertEquals("ActionRule{#children=0, skippable=false}", rule2.toString());
		assertEquals("ActionRule{#children=0, skippable=false}", rule3.toString());
	}
	
}

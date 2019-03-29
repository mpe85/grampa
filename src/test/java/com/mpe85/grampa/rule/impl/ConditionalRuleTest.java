package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.ActionContext;

public class ConditionalRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		final Predicate<ActionContext<String>> pred = ctx -> true;
		final EmptyRule<String> empty = new EmptyRule<>();
		final NeverRule<String> never = new NeverRule<>();
		
		final ConditionalRule<String> rule1 = new ConditionalRule<>(pred, empty, never);
		final ConditionalRule<String> rule2 = new ConditionalRule<>(pred, new EmptyRule<>(), new NeverRule<>());
		final ConditionalRule<String> rule3 = new ConditionalRule<>(pred, empty);
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals(
				"ConditionalRule{#children=2, thenRule=EmptyRule{#children=0}, elseRule=NeverRule{#children=0}}",
				rule1.toString());
		assertEquals(
				"ConditionalRule{#children=2, thenRule=EmptyRule{#children=0}, elseRule=NeverRule{#children=0}}",
				rule2.toString());
		assertEquals(
				"ConditionalRule{#children=1, thenRule=EmptyRule{#children=0}, elseRule=null}",
				rule3.toString());
	}
	
}

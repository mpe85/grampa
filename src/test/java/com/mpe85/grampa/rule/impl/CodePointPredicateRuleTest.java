package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

public class CodePointPredicateRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		
		final Predicate<Integer> pred = cp -> cp == 'a';
		final CodePointPredicateRule<String> rule1 = new CodePointPredicateRule<>(pred);
		final CodePointPredicateRule<String> rule2 = new CodePointPredicateRule<>(pred);
		final CodePointPredicateRule<String> rule3 = new CodePointPredicateRule<>('a');
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals("CodePointPredicateRule{#children=0}", rule1.toString());
		assertEquals("CodePointPredicateRule{#children=0}", rule2.toString());
		assertEquals("CodePointPredicateRule{#children=0}", rule3.toString());
	}
	
}

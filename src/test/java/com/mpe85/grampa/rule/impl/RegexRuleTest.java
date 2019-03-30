package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class RegexRuleTest {
	
	@Test
	public void equalsHashCodeToString() {
		final Pattern pattern = Pattern.compile("[a]{3}");
		
		final RegexRule<String> rule1 = new RegexRule<>(pattern);
		final RegexRule<String> rule2 = new RegexRule<>(pattern);
		final RegexRule<String> rule3 = new RegexRule<>("[a]{3}");
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
		
		assertEquals("RegexRule{#children=0, pattern=[a]{3}}", rule1.toString());
		assertEquals("RegexRule{#children=0, pattern=[a]{3}}", rule2.toString());
		assertEquals("RegexRule{#children=0, pattern=[a]{3}}", rule3.toString());
	}
	
}

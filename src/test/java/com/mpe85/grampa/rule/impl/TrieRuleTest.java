package com.mpe85.grampa.rule.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Sets;

public class TrieRuleTest {
	
	@Test
	public void equalsHashCode() {
		final TrieRule<String> rule1 = new TrieRule<>("foo", "bar");
		final TrieRule<String> rule2 = new TrieRule<>(Sets.newHashSet("bar", "foo"));
		final TrieRule<String> rule3 = new TrieRule<>("foobar");
		
		assertTrue(rule1.equals(rule2));
		assertFalse(rule1.equals(rule3));
		assertFalse(rule1.equals(new Object()));
		assertEquals("TrieRule{#children=0, strings=[bar, foo], ignoreCase=false}", rule1.toString());
		assertEquals("TrieRule{#children=0, strings=[bar, foo], ignoreCase=false}", rule2.toString());
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
		assertNotEquals(rule1.hashCode(), rule3.hashCode());
	}
	
}

package com.mpe85.grampa.rule.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TrieRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val rule1 = TrieRule<String>("foo", "bar")
    val rule2 = TrieRule<String>(setOf("bar", "foo"))
    val rule3 = TrieRule<String>("foobar")
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals("TrieRule(strings=[bar, foo], ignoreCase=false)", rule1.toString())
    assertEquals("TrieRule(strings=[bar, foo], ignoreCase=false)", rule2.toString())
    assertEquals("TrieRule(strings=[foobar], ignoreCase=false)", rule3.toString())
  }
}
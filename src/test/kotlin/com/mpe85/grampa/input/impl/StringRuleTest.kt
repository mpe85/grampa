package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.impl.StringRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class StringRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val rule1 = StringRule<String>("string")
    val rule2 = StringRule<String>("string", false)
    val rule3 = StringRule<String>("string", true)
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals("StringRule(string=string, ignoreCase=false)", rule1.toString())
    assertEquals("StringRule(string=string, ignoreCase=false)", rule2.toString())
    assertEquals("StringRule(string=string, ignoreCase=true)", rule3.toString())
  }
}
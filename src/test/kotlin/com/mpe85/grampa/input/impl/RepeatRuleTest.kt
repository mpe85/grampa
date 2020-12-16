package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.RepeatRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RepeatRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val empty: Rule<String> = EmptyRule()
    val rule1 = RepeatRule(empty, 0, 5)
    val rule2 = RepeatRule(EmptyRule<String>(), 0, 5)
    val rule3 = RepeatRule(empty, 2, null)
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals("RepeatRule{#children=1, min=0, max=5}", rule1.toString())
    assertEquals("RepeatRule{#children=1, min=0, max=5}", rule2.toString())
    assertEquals("RepeatRule{#children=1, min=2, max=null}", rule3.toString())
  }

  @Test
  fun create_invalid_min() {
    assertThrows(IllegalArgumentException::class.java) { RepeatRule(EmptyRule<Any>(), -1, null) }
  }

  @Test
  fun create_invalid_max() {
    assertThrows(IllegalArgumentException::class.java) { RepeatRule(EmptyRule<Any>(), 3, 2) }
  }
}
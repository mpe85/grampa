package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.impl.ActionRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ActionRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val action = Action { _: ActionContext<String> -> true }
    val rule1 = ActionRule(action)
    val rule2 = ActionRule(action, false)
    val rule3 = ActionRule({ _: ActionContext<String> -> true })
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals("ActionRule{#children=0, skippable=false}", rule1.toString())
    assertEquals("ActionRule{#children=0, skippable=false}", rule2.toString())
    assertEquals("ActionRule{#children=0, skippable=false}", rule3.toString())
  }
}
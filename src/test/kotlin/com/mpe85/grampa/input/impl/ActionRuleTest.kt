package com.mpe85.grampa.input.impl

import com.mpe85.grampa.context.RuleContext
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.impl.ActionRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ActionRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val action = Action { _: RuleContext<String> -> true }
    val rule1 = ActionRule(action::run)
    val rule2 = ActionRule(action::run, false)
    val rule3 = ActionRule({ _: RuleContext<String> -> true })
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals(
      "ActionRule(action=fun com.mpe85.grampa.rule.Action<T>.run(com.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)",
      rule1.toString()
    )
    assertEquals(
      "ActionRule(action=fun com.mpe85.grampa.rule.Action<T>.run(com.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)",
      rule2.toString()
    )
    assertEquals(
      "ActionRule(action=(com.mpe85.grampa.context.RuleContext<kotlin.String>) -> kotlin.Boolean, skippable=false)",
      rule3.toString()
    )
  }
}
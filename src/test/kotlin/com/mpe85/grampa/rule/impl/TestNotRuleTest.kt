package com.mpe85.grampa.rule.impl

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestNotRuleTest {
  @Test
  fun isPredicate() {
    assertTrue(TestNotRule(EmptyRule<Unit>()).testRule)
  }
}
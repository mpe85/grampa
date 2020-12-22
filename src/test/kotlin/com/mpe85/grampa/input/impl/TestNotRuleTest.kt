package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.TestNotRule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TestNotRuleTest {
  @Test
  fun isPredicate() {
    assertTrue(TestNotRule(EmptyRule<Unit>()).testRule)
  }
}
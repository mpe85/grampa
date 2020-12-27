package com.mpe85.grampa.parser

import com.mpe85.grampa.grammar.Grammar
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.StringRule
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParseRunnerTest {
  @Test
  fun test_run() {
    val parser = object : Grammar<Unit> {
      override fun root(): Rule<Unit> {
        return StringRule("foo")
      }
    }
    val runner = Parser(parser)
    assertTrue(runner.run("foo").matched)
    assertTrue(runner.run("foobar").matched)
    assertFalse(runner.run("bar").matched)
  }
}

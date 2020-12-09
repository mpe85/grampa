package com.mpe85.grampa.runner

import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.StringRule
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ParseRunnerTest {
  @Test
  fun test_run() {
    val parser = Parser { StringRule<Void?>("foo") }
    val runner = DefaultParseRunner(parser)
    assertTrue(runner.run("foo").matched)
    assertTrue(runner.run("foobar").matched)
    assertFalse(runner.run("bar").matched)
  }
}

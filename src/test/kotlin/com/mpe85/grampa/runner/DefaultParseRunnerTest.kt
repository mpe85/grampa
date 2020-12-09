package com.mpe85.grampa.runner

import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PreMatchEvent
import com.mpe85.grampa.parser.AbstractParser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.EmptyRule
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

@SuppressFBWarnings(
  value = ["SIC_INNER_SHOULD_BE_STATIC_ANON"],
  justification = "Performance is not of great importance in unit tests."
)
class DefaultParseRunnerTest {

  private class IntegerTestListener : ParseEventListener<Int>() {
    override fun beforeMatch(event: PreMatchEvent<Int>) {
      throw RuntimeException()
    }
  }

  @get:Test
  val rootRule: Unit
    get() {
      class Parser : AbstractParser<Int>() {
        override fun root(): Rule<Int> {
          return empty()
        }
      }

      val runner = DefaultParseRunner<Int>(Parser())
      assertTrue(runner.rootRule is EmptyRule<*>)
    }

  @Test
  fun registerListener() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return empty()
      }
    }

    val runner = DefaultParseRunner<Int>(
      Parser(),
      { ex, _ -> assertTrue(ex is RuntimeException) })
    val listener = IntegerTestListener()
    runner.registerListener(listener)
    assertDoesNotThrow<ParseResult<Int>> { runner.run("a") }
  }

  @Test
  fun unregisterListener() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return empty()
      }
    }

    val runner = DefaultParseRunner<Int>(
      Parser(),
      { ex, _ -> fail("Listener should not have been called.", ex) })
    val listener = IntegerTestListener()
    runner.registerListener(listener)
    runner.unregisterListener(listener)
    runner.run("a")
  }

}

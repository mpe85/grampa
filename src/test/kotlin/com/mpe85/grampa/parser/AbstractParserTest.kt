package com.mpe85.grampa.parser

import com.google.common.collect.Sets
import com.google.common.eventbus.Subscribe
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.exception.ActionRunException
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.ActionRule
import com.mpe85.grampa.runner.DefaultParseRunner
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import java.util.concurrent.atomic.AtomicReference
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@SuppressFBWarnings(
  value = ["SIC_INNER_SHOULD_BE_STATIC_ANON"],
  justification = "Performance is not of great importance in unit tests."
)
class AbstractParserTest {
  private class IntegerTestListener : ParseEventListener<Int>()
  private class CharSequenceTestListener : ParseEventListener<CharSequence>()

  @Test
  fun empty_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return empty()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("foo", result.restOfInput)
  }

  @Test
  fun never_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return never()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("foo", result.restOfInput)
  }

  @Test
  fun eoi_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(string("foo"), eoi())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("foo", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun eoi_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(string("foo"), eoi())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo ")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("foo ", result.restOfInput)
  }

  @Test
  fun anyChar_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyChar()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("f", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyChar_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyChar()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyCodePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyCodePoint()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\uD835\uDD38", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyCodePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyCodePoint()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun character_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return character('f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("f", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun character_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return character('f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("g")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("g", result.restOfInput)
  }

  @Test
  fun ignoreCase_character_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("F")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("F", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_character_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("G")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("G", result.restOfInput)
  }

  @Test
  fun charRange_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return charRange('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("c", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun charRange_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return charRange('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("h")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("h", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars(Sets.newHashSet('a', 'f'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars("a")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_invalid_wrongChar() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("c", result.restOfInput)
  }

  @Test
  fun anyOfChars_invalid_never() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars("")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("a", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("c", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars(Sets.newHashSet('a', 'f'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("c", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_any() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars("")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("c", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("f", result.restOfInput)
  }

  @Test
  fun codePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePoint("\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\uD835\uDD38", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun codePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePoint("\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD39")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\uD835\uDD39", result.restOfInput)
  }

  @Test
  fun ignoreCase_codePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f'.toInt())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("F")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("F", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_codePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f'.toInt())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("G")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("G", result.restOfInput)
  }

  @Test
  fun codePointRange_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('Z'.toInt(), 'b'.toInt())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun codePointRange_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('Z'.toInt(), 'b'.toInt())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("X")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("X", result.restOfInput)
  }

  @Test
  fun codePointRange_illegalArgument() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('b'.toInt(), 'a'.toInt())
      }
    }
    Assertions.assertThrows(IllegalArgumentException::class.java) {
      DefaultParseRunner<Int>(
        Parser()
      ).run("a")
    }
  }

  @Test
  fun anyOfCodePoint_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\uD835\uDD38", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoint_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints(Sets.newHashSet('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\uD835\uDD38", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoint_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints("\uD835\uDD38")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\uD835\uDD38", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoints_invalid_wrongCp() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("b", result.restOfInput)
  }

  @Test
  fun anyOfCodePoints_invalid_never() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints("")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("b", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("b", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints("a\uD835\uDD38")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("b", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints(Sets.newHashSet('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("b", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_any() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("b", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\uD835\uDD38", result.restOfInput)
  }

  @Test
  fun string_valid_nonEmpty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("foobar")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobart")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("foobar", result.matchedInput)
    Assertions.assertEquals("t", result.restOfInput)
  }

  @Test
  fun string_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abc")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("abc", result.restOfInput)
  }

  @Test
  fun string_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("foobar")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobär")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("foobär", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_nonEmpty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("foobar")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObAr")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("fOObAr", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aBc")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("aBc", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_oneChar() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("c")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("c", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("fOObAr")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObÄr")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("fOObÄr", result.restOfInput)
  }

  @Test
  fun regex_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return regex("abc+")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcccccd")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("abccccc", result.matchedInput)
    Assertions.assertEquals("d", result.restOfInput)
  }

  @Test
  fun regex_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("abc+")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ab")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("ab", result.restOfInput)
  }

  @Test
  fun strings_valid_vararg() {
    val stringsRuleMatch = AtomicReference<CharSequence?>()

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          strings("football", "foo", "foobar"),
          command { ctx: ActionContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobaz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("foobaz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
    Assertions.assertEquals("foo", stringsRuleMatch.get())
  }

  @Test
  fun strings_valid_set_oneString() {
    val stringsRuleMatch = AtomicReference<CharSequence?>()

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          strings("foo"),
          command { ctx: ActionContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobaz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("foobaz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
    Assertions.assertEquals("foo", stringsRuleMatch.get())
  }

  @Test
  fun strings_invalid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return strings("football", "foo", "foobar")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fo")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("fo", result.restOfInput)
  }

  @Test
  fun strings_invalid_set_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return strings(Sets.newHashSet())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fo")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("fo", result.restOfInput)
  }

  @Test
  fun ignoreCase_strings_valid_vararg() {
    val stringsRuleMatch = AtomicReference<CharSequence?>()

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          ignoreCase("football", "foo", "foobar"),
          command { ctx: ActionContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObaz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("fOObaz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
    Assertions.assertEquals("fOO", stringsRuleMatch.get())
  }

  @Test
  fun ignoreCase_strings_valid_set_oneString() {
    val stringsRuleMatch = AtomicReference<CharSequence?>()

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          ignoreCase(Sets.newHashSet("foo")),
          command { ctx: ActionContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObaz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("fOObaz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
    Assertions.assertEquals("fOO", stringsRuleMatch.get())
  }

  @Test
  fun ignoreCase_strings_invalid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("football", "foo", "foobar")
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ignoreCase_strings_invalid_set_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase(Sets.newHashSet())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ascii_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("#")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("#", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun ascii_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("ß", result.restOfInput)
  }

  @Test
  fun bmp_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("ß", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun bmp_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\uD835\uDD38", result.restOfInput)
  }

  @Test
  fun digit_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("5")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("5", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun digit_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("O")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("O", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ä")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("ä", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("1", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("1", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("(")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("(", result.restOfInput)
  }

  @Test
  fun letter_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("Ü")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("Ü", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun letter_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("$")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("$", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_letter() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("x")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("x", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_digit() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("9")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("9", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("%")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("%", result.restOfInput)
  }

  @Test
  fun printable_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("n")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("n", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun printable_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\n", result.restOfInput)
  }

  @Test
  fun spaceChar_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run(" ")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals(" ", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun spaceChar_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\n", result.restOfInput)
  }

  @Test
  fun whitespace_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\n", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun whitespace_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("_")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("_", result.restOfInput)
  }

  @Test
  fun cr_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\r", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun cr_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\n", result.restOfInput)
  }

  @Test
  fun lf_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\n", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun lf_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\r", result.restOfInput)
  }

  @Test
  fun crlf_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r\n")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("\r\n", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun crlf_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n\r")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("\n\r", result.restOfInput)
  }

  @Test
  fun sequence_valid_character() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          character('a'),
          character('b'),
          character('c')
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("abc", result.matchedInput)
    Assertions.assertEquals("d", result.restOfInput)
  }

  @Test
  fun sequence_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("abcd", result.restOfInput)
  }

  @Test
  fun sequence_valid_push() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          push { ctx: ActionContext<Int>? -> peek(ctx!!)!! + 4 },
          sequence(
            push { ctx: ActionContext<Int>? -> pop(1, ctx!!)!! + peek(ctx)!! }),
          optional(action { ctx: ActionContext<Int> ->
            ctx.stack.push(0)
            false
          })
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("whatever", result.restOfInput)
    Assertions.assertEquals(Integer.valueOf(9426), result.stackTop)
    Assertions.assertEquals(2, result.stack.size)
    Assertions.assertEquals(Integer.valueOf(9426), result.stack.peek())
    Assertions.assertEquals(Integer.valueOf(4715), result.stack.peek(1))
  }

  @Test
  fun sequence_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          character('a'),
          character('b'),
          character('c')
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("acdc")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("acdc", result.restOfInput)
  }

  @Test
  fun firstOf_valid_sequence() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          firstOf(
            sequence(string("foo"), string("bar")),
            sequence(string("foo"), string("baz"))
          ),
          string("xxx")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobazxxx")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("foobazxxx", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun firstOf_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return firstOf()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("foo", result.restOfInput)
  }

  @Test
  fun firstOf_valid_oneRule() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return firstOf(string("foo"))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("foo", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun firstOf_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return firstOf(
          string("foo"),
          string("bar"),
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("babafoo")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("babafoo", result.restOfInput)
  }

  @Test
  fun optional_valid_match() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun optional_valid_noMatch() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_zero() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("", result.matchedInput)
    Assertions.assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_more() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("aaaaa", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_one() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("a", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_more() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("aaaaa", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("b", result.restOfInput)
  }

  @Test
  fun repeat_valid_times() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(4)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("zzzz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_times() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(6, 7)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("zzzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_range() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(4, 7)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("zzzzz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_range() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(2, 4)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("z")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("z", result.restOfInput)
  }

  @Test
  fun repeat_valid_max() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).max(3)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("zz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_max() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return sequence(repeat(character('z')).max(3), eoi())
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("zzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_min() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(3)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("zzzzz", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_min() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(8)
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("zzzzz", result.restOfInput)
  }

  @Test
  fun test_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          test(string("what")),
          string("whatever")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("whatever", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun test_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          test(string("ever")),
          string("whatever")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun testNot_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          testNot(string("foo")),
          string("whatever")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("whatever", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun testNot_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          testNot(string("what")),
          string("whatever")
        )
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_withElse() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, letter(), digit())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("z", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_noElse() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, letter())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("z", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_else() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex != 0 }, letter(), digit())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    Assertions.assertTrue(result.matched)
    Assertions.assertTrue(result.matchedEntireInput)
    Assertions.assertEquals("1", result.matchedInput)
    Assertions.assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, never(), empty())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    Assertions.assertFalse(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertNull(result.matchedInput)
    Assertions.assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun action_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          Assertions.assertEquals(0, ctx.level)
          Assertions.assertNotNull(ctx.position)
          true
        }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_failingAction() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          Assertions.assertEquals(0, ctx.level)
          Assertions.assertNotNull(ctx.position)
          false
        }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_illegalAdvanceIndex() {
    class EvilActionRule(action: Action<Int>) : ActionRule<Int>(action) {
      override fun match(context: RuleContext<Int>): Boolean {
        return super.match(context) && context.advanceIndex(1000)
      }
    }

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return EvilActionRule { true }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertFalse(runner.run("whatever").matched)
  }

  @Test
  fun command_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return command { ctx: ActionContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_valid_noSkip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return skippableAction { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          true
        }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_valid_skip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return test(
          skippableAction { ctx: ActionContext<Int> ->
            ctx.stack.push(4711)
            true
          })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return skippableAction { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          false
        }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_noSkip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return skippableCommand { ctx: ActionContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_skip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return test(
          skippableCommand { ctx: ActionContext<Int> -> ctx.stack.push(4711) })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun post_valid_suppliedEvent() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          string("whatever"),
          post { ctx: ActionContext<Int> -> ctx.previousMatch!! })
      }
    }

    class Listener : ParseEventListener<Int>() {
      var string: String? = null
        private set

      @Subscribe
      @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
      fun stringEvent(event: String?) {
        string = event
      }

      override fun afterMatchSuccess(event: MatchSuccessEvent<Int>) {
        Assertions.assertNotNull(event.context)
      }

      override fun afterParse(event: PostParseEvent<Int>) {
        Assertions.assertNotNull(event.result)
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    Assertions.assertEquals("whatever", listener.string)
  }

  @Test
  fun post_valid_staticEvent() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          string("whatever"),
          post("someEvent")
        )
      }
    }

    class Listener : ParseEventListener<Int>() {
      var string: String? = null
        private set

      @Subscribe
      @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
      fun stringEvent(event: String?) {
        string = event
      }

      override fun afterMatchSuccess(event: MatchSuccessEvent<Int>) {
        Assertions.assertNotNull(event.context)
      }

      override fun afterParse(event: PostParseEvent<Int>) {
        Assertions.assertNotNull(event.result)
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    Assertions.assertEquals("someEvent", listener.string)
  }

  @Test
  fun pop_valid_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), pop())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun pop_valid_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), pop(1))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke { 4712 })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    Assertions.assertEquals(1, runner.run("whatever").stack.size)
  }

  @Test
  fun pop_valid_action() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          action { ctx: ActionContext<Int> -> pop(ctx) == 4711 })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_top() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          action { ctx: ActionContext<Number> ->
            popAs(Int::class.javaObjectType, ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner<Number>(Parser())
    Assertions.assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_down() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: ActionContext<Number> ->
            popAs(Int::class.javaObjectType, 1, ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner<Number>(Parser())
    Assertions.assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun peek_valid_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), action { ctx: ActionContext<Int> ->
          peek(ctx) == 4711
        })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(4711, runner.run("whatever").stackTop)
  }

  @Test
  fun peek_valid_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: ActionContext<Int> -> peek(1, ctx) == 4711 })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_top() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(push(4711), action { ctx: ActionContext<Number> ->
          peekAs(Int::class.javaObjectType, ctx) == 4711
        })
      }
    }

    val runner = DefaultParseRunner<Number>(Parser())
    Assertions.assertEquals(4711, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_down() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: ActionContext<Number> ->
            peekAs(Int::class.javaObjectType, 1, ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner<Number>(Parser())
    Assertions.assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1) { 4713 })
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    Assertions.assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke(4712))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    Assertions.assertEquals(1, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1, 4713))
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    Assertions.assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return poke(4712)
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertThrows(ActionRunException::class.java) { runner.run("whatever") }
  }

  @Test
  fun push_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return push(4711)
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun dup_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), dup())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(2, runner.run("whatever").stack.size)
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun dup_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return dup()
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertThrows(ActionRunException::class.java) { runner.run("whatever") }
  }

  @Test
  fun swap_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), swap())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertEquals(2, runner.run("whatever").stack.size)
    Assertions.assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    Assertions.assertEquals(Integer.valueOf(4712), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun swap_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), swap())
      }
    }

    val runner = DefaultParseRunner<Int>(Parser())
    runner.registerListener(IntegerTestListener())
    Assertions.assertThrows(ActionRunException::class.java) { runner.run("whatever") }
  }

  @Test
  fun previousMatch_valid() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return sequence(
          string("hello"),
          string("world"),
          push { ctx: ActionContext<CharSequence> -> ctx.parent?.previousMatch!! },
          sequence(
            string("foo"),
            string("bar")
          ),
          push { ctx: ActionContext<CharSequence> -> ctx.parent?.previousMatch!! },
          test(string("baz")),
          push { ctx: ActionContext<CharSequence> -> ctx.parent?.previousMatch!! },
          sequence(
            test(string("ba")),
            string("b"),
            test(string("az"))
          ),
          push { ctx: ActionContext<CharSequence> -> ctx.parent?.previousMatch!! })
      }
    }

    val runner = DefaultParseRunner<CharSequence>(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("helloworldfoobarbaz")
    Assertions.assertTrue(result.matched)
    Assertions.assertFalse(result.matchedEntireInput)
    Assertions.assertEquals("b", result.stack.pop())
    Assertions.assertEquals("foobar", result.stack.pop())
    Assertions.assertEquals("foobar", result.stack.pop())
    Assertions.assertEquals("world", result.stackTop)
  }
}
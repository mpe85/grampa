package com.mpe85.grampa.parser

import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.ActionContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.ActionRule
import com.mpe85.grampa.runner.DefaultParseRunner
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import java.util.concurrent.atomic.AtomicReference
import org.greenrobot.eventbus.Subscribe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("foo", result.restOfInput)
  }

  @Test
  fun never_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return never()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("foo", result.restOfInput)
  }

  @Test
  fun eoi_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(string("foo"), eoi())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foo", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun eoi_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(string("foo"), eoi())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo ")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("foo ", result.restOfInput)
  }

  @Test
  fun anyChar_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyChar()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("f", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyChar_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyChar()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyCodePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyCodePoint()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\uD835\uDD38", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyCodePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyCodePoint()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun character_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return character('f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("f", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun character_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return character('f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("g")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("g", result.restOfInput)
  }

  @Test
  fun ignoreCase_character_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("F")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("F", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_character_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("G")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("G", result.restOfInput)
  }

  @Test
  fun charRange_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return charRange('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("c", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun charRange_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return charRange('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("h")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("h", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars(setOf('a', 'f'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars("a")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfChars_invalid_wrongChar() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("c", result.restOfInput)
  }

  @Test
  fun anyOfChars_invalid_never() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfChars("")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("a", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("c", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars(setOf('a', 'f'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("c", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_valid_any() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars("")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("c", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfChars_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfChars('a', 'f')
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("f")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("f", result.restOfInput)
  }

  @Test
  fun codePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePoint("\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\uD835\uDD38", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun codePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePoint("\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD39")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\uD835\uDD39", result.restOfInput)
  }

  @Test
  fun ignoreCase_codePoint_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f'.toInt())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("F")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("F", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_codePoint_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase('f'.toInt())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("G")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("G", result.restOfInput)
  }

  @Test
  fun codePointRange_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('Z'.toInt(), 'b'.toInt())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun codePointRange_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('Z'.toInt(), 'b'.toInt())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("X")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("X", result.restOfInput)
  }

  @Test
  fun codePointRange_illegalArgument() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return codePointRange('b'.toInt(), 'a'.toInt())
      }
    }
    assertThrows(IllegalArgumentException::class.java) {
      DefaultParseRunner(Parser()).run("a")
    }
  }

  @Test
  fun anyOfCodePoint_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\uD835\uDD38", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoint_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints(setOf('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\uD835\uDD38", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoint_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints("\uD835\uDD38")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\uD835\uDD38", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun anyOfCodePoints_invalid_wrongCp() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun anyOfCodePoints_invalid_never() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return anyOfCodePoints("")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("b", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_string() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints("a\uD835\uDD38")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("b", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_set() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints(setOf('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("b", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_valid_any() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("b", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun noneOfCodePoints_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return noneOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\uD835\uDD38", result.restOfInput)
  }

  @Test
  fun string_valid_nonEmpty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("foobar")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobart")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("foobar", result.matchedInput)
    assertEquals("t", result.restOfInput)
  }

  @Test
  fun string_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abc")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("abc", result.restOfInput)
  }

  @Test
  fun string_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("foobar")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobär")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("foobär", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_nonEmpty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("foobar")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObAr")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("fOObAr", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aBc")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("aBc", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_valid_oneChar() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("c")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("c")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("c", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ignoreCase_string_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("fOObAr")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObÄr")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fOObÄr", result.restOfInput)
  }

  @Test
  fun regex_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return regex("abc+")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcccccd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("abccccc", result.matchedInput)
    assertEquals("d", result.restOfInput)
  }

  @Test
  fun regex_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return string("abc+")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ab")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("ab", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobaz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foobaz", result.matchedInput)
    assertEquals("", result.restOfInput)
    assertEquals("foo", stringsRuleMatch.get())
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobaz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foobaz", result.matchedInput)
    assertEquals("", result.restOfInput)
    assertEquals("foo", stringsRuleMatch.get())
  }

  @Test
  fun strings_invalid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return strings("football", "foo", "foobar")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fo", result.restOfInput)
  }

  @Test
  fun strings_invalid_set_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return strings(setOf())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fo", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObaz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("fOObaz", result.matchedInput)
    assertEquals("", result.restOfInput)
    assertEquals("fOO", stringsRuleMatch.get())
  }

  @Test
  fun ignoreCase_strings_valid_set_oneString() {
    val stringsRuleMatch = AtomicReference<CharSequence?>()

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          ignoreCase(setOf("foo")),
          command { ctx: ActionContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fOObaz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("fOObaz", result.matchedInput)
    assertEquals("", result.restOfInput)
    assertEquals("fOO", stringsRuleMatch.get())
  }

  @Test
  fun ignoreCase_strings_invalid_vararg() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("football", "foo", "foobar")
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ignoreCase_strings_invalid_set_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase(setOf())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ascii_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("#")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("#", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ascii_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("ß", result.restOfInput)
  }

  @Test
  fun bmp_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("ß", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun bmp_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\uD835\uDD38", result.restOfInput)
  }

  @Test
  fun digit_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("5")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("5", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun digit_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("O")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("O", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ä")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("ä", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("1", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("1", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("(")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("(", result.restOfInput)
  }

  @Test
  fun letter_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("Ü")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("Ü", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letter_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("$")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("$", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_letter() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("x")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("x", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_digit() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("9")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("9", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("%")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("%", result.restOfInput)
  }

  @Test
  fun printable_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun printable_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun spaceChar_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run(" ")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals(" ", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun spaceChar_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun whitespace_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun whitespace_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("_")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("_", result.restOfInput)
  }

  @Test
  fun cr_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\r", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun cr_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun lf_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun lf_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\r", result.restOfInput)
  }

  @Test
  fun crlf_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\r\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun crlf_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n\r")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n\r", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("abc", result.matchedInput)
    assertEquals("d", result.restOfInput)
  }

  @Test
  fun sequence_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("abcd", result.restOfInput)
  }

  @Test
  fun sequence_valid_push() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          push { ctx: ActionContext<Int> -> peek(ctx) + 4 },
          sequence(push { ctx: ActionContext<Int> -> pop(1, ctx) + peek(ctx) }),
          optional(action { ctx: ActionContext<Int> ->
            ctx.stack.push(0)
            false
          })
        )
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("whatever", result.restOfInput)
    assertEquals(Integer.valueOf(9426), result.stackTop)
    assertEquals(2, result.stack.size)
    assertEquals(Integer.valueOf(9426), result.stack.peek())
    assertEquals(Integer.valueOf(4715), result.stack.peek(1))
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("acdc")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("acdc", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobazxxx")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foobazxxx", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun firstOf_valid_empty() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return firstOf()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("foo", result.restOfInput)
  }

  @Test
  fun firstOf_valid_oneRule() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return firstOf(string("foo"))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foo", result.matchedInput)
    assertEquals("", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("babafoo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("babafoo", result.restOfInput)
  }

  @Test
  fun optional_valid_match() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun optional_valid_noMatch() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_zero() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_more() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("aaaaa", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_one() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_more() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("aaaaa", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun repeat_valid_times() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')) * 4
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_times() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(6, 7)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_range() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(4, 7)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_range() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(2, 4)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("z")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("z", result.restOfInput)
  }

  @Test
  fun repeat_valid_max() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).max(3)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_max() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return sequence(repeat(character('z')).max(3), eoi())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_min() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(3)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_min() {
    class Parser : AbstractParser<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(8)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzzz", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("whatever", result.matchedInput)
    assertEquals("", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("whatever", result.matchedInput)
    assertEquals("", result.restOfInput)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_withElse() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, letter(), digit())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("z", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_noElse() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, letter())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("z", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_else() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex != 0 }, letter(), digit())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("1", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: ActionContext<Int> -> ctx.startIndex == 0 }, never(), empty())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun action_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          assertEquals(0, ctx.level)
          assertNotNull(ctx.position)
          true
        }
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_failingAction() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: ActionContext<Int> ->
          ctx.stack.push(4711)
          assertEquals(0, ctx.level)
          assertNotNull(ctx.position)
          false
        }
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_illegalAdvanceIndex() {
    class EvilActionRule(action: Action<Int>) : ActionRule<Int>(action::run) {
      override fun match(context: RuleContext<Int>): Boolean {
        return super.match(context) && context.advanceIndex(1000)
      }
    }

    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return EvilActionRule { true }
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertFalse(runner.run("whatever").matched)
  }

  @Test
  fun command_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return command { ctx: ActionContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_noSkip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return skippableCommand { ctx: ActionContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_skip() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return test(
          skippableCommand { ctx: ActionContext<Int> -> ctx.stack.push(4711) })
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
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
      fun stringEvent(event: String) {
        string = event
      }

      override fun afterMatchSuccess(event: MatchSuccessEvent<Int>) {
        assertNotNull(event.context)
      }

      override fun afterParse(event: PostParseEvent<Int>) {
        assertNotNull(event.result)
      }
    }

    val runner = DefaultParseRunner(Parser())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    assertEquals("whatever", listener.string)
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
      fun stringEvent(event: String) {
        string = event
      }

      override fun afterMatchSuccess(event: MatchSuccessEvent<Int>) {
        assertNotNull(event.context)
      }

      override fun afterParse(event: PostParseEvent<Int>) {
        assertNotNull(event.result)
      }
    }

    val runner = DefaultParseRunner(Parser())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    assertEquals("someEvent", listener.string)
  }

  @Test
  fun pop_valid_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), pop())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun pop_valid_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), pop(1))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke { 4712 })
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(1, runner.run("whatever").stack.size)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_top() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          action { ctx: ActionContext<Number> ->
            pop(ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner(Parser())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_down() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: ActionContext<Number> ->
            pop(1, ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner(Parser())
    assertEquals(4712, runner.run("whatever").stackTop)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(4711, runner.run("whatever").stackTop)
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_top() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(push(4711), action { ctx: ActionContext<Number> ->
          peek(ctx) == 4711
        })
      }
    }

    val runner = DefaultParseRunner(Parser())
    assertEquals(4711, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_down() {
    class Parser : AbstractParser<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: ActionContext<Number> ->
            peek(1, ctx) == 4711
          })
      }
    }

    val runner = DefaultParseRunner(Parser())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1) { 4713 })
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_top() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke(4712))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(1, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_down() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1, 4713))
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return poke(4712)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
  }

  @Test
  fun push_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return push(4711)
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun dup_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), dup())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(2, runner.run("whatever").stack.size)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun dup_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return dup()
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
  }

  @Test
  fun swap_valid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), swap())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertEquals(2, runner.run("whatever").stack.size)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun swap_invalid() {
    class Parser : AbstractParser<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), swap())
      }
    }

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
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

    val runner = DefaultParseRunner(Parser())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("helloworldfoobarbaz")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("b", result.stack.pop())
    assertEquals("foobar", result.stack.pop())
    assertEquals("foobar", result.stack.pop())
    assertEquals("world", result.stackTop)
  }
}

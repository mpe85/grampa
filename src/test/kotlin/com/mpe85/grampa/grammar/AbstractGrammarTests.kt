package com.mpe85.grampa.grammar

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.context.RuleContext
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Action
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.ActionRule
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import java.util.concurrent.atomic.AtomicReference
import org.greenrobot.eventbus.Subscribe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private class IntegerTestListener : ParseEventListener<Int>()
private class CharSequenceTestListener : ParseEventListener<CharSequence>()

class AbstractGrammarTests : StringSpec({
  "Empty rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = empty()
    }).apply {
      registerListener(IntegerTestListener())
      checkAll<String> { str ->
        run(str).apply {
          matched shouldBe true
          matchedEntireInput shouldBe str.isEmpty()
          matchedInput shouldBe ""
          restOfInput shouldBe str
        }
      }
    }
  }
  "Never rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = never()
    }).apply {
      registerListener(IntegerTestListener())
      checkAll<String> { str ->
        run(str).apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe str
        }
      }
    }
  }
  "EOI rule grammar" {
    checkAll<String> { str ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = sequence(string(str), eoi())
      }).apply {
        registerListener(IntegerTestListener())
        run(str).apply {
          matched shouldBe true
          matchedEntireInput shouldBe true
          matchedInput shouldBe str
          restOfInput shouldBe ""
        }
        run("$str ").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "$str "
        }
      }
    }
  }
  "AnyChar rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = anyChar()
    }).apply {
      registerListener(IntegerTestListener())
      run("f").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "f"
        restOfInput shouldBe ""
      }
      run("").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe ""
      }
    }
  }
  "AnyCodePoint rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = anyCodePoint()
    }).apply {
      registerListener(IntegerTestListener())
      run("\uD835\uDD38").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\uD835\uDD38"
        restOfInput shouldBe ""
      }
      run("").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe ""
      }
    }
  }
  "Character rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = character('f')
    }).apply {
      registerListener(IntegerTestListener())
      run("f").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "f"
        restOfInput shouldBe ""
      }
      run("g").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "g"
      }
    }
  }
  "CharacterIgnoreCase rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = ignoreCase('f')
    }).apply {
      registerListener(IntegerTestListener())
      run("F").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "F"
        restOfInput shouldBe ""
      }
      run("G").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "G"
      }
    }
  }
  "CharRange rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = charRange('a', 'f')
    }).apply {
      registerListener(IntegerTestListener())
      run("c").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "c"
        restOfInput shouldBe ""
      }
      run("h").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "h"
      }
    }
  }
  "AnyOfChars rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfChars('a', 'f')
      },
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfChars(setOf('a', 'f'))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfChars("af")
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("a").apply {
          matched shouldBe true
          matchedEntireInput shouldBe true
          matchedInput shouldBe "a"
          restOfInput shouldBe ""
        }
        run("c").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "c"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = anyOfChars()
    }).apply {
      registerListener(IntegerTestListener())
      run("a").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "a"
      }
    }
  }
  "NoneOfChars rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfChars('a', 'f')
      },
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfChars(setOf('a', 'f'))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfChars("af")
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("c").apply {
          matched shouldBe true
          matchedEntireInput shouldBe true
          matchedInput shouldBe "c"
          restOfInput shouldBe ""
        }
        run("f").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "f"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = noneOfChars()
    }).apply {
      registerListener(IntegerTestListener())
      run("c").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "c"
        restOfInput shouldBe ""
      }
    }
  }
  "CodePoint rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = codePoint("\uD835\uDD38".codePointAt(0))
    }).apply {
      registerListener(IntegerTestListener())
      run("\uD835\uDD38").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\uD835\uDD38"
        restOfInput shouldBe ""
      }
      run("\uD835\uDD39").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\uD835\uDD39"
      }
    }
  }
  "CodePoint ignoreCase rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = ignoreCase('f'.toInt())
    }).apply {
      registerListener(IntegerTestListener())
      run("F").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "F"
        restOfInput shouldBe ""
      }
      run("G").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "G"
      }
    }
  }
  "CodePointRange rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = codePointRange('Z'.toInt(), 'b'.toInt())
    }).apply {
      registerListener(IntegerTestListener())
      run("a").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "a"
        restOfInput shouldBe ""
      }
      run("X").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "X"
      }
    }
    shouldThrow<IllegalArgumentException> {
      Parser(object : AbstractGrammar<Int>() {
        override fun root(): Rule<Int> = codePointRange('b'.toInt(), 'a'.toInt())
      })
    }
  }
  "AnyOfCodePoints rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfCodePoints(setOf('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = anyOfCodePoints("\uD835\uDD38")
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("\uD835\uDD38").apply {
          matched shouldBe true
          matchedEntireInput shouldBe true
          matchedInput shouldBe "\uD835\uDD38"
          restOfInput shouldBe ""
        }
        run("b").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "b"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = anyOfCodePoints("")
    }).apply {
      registerListener(IntegerTestListener())
      run("b").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "b"
      }
    }
  }
  "NoneOfCodePoints rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfCodePoints('a'.toInt(), "\uD835\uDD38".codePointAt(0))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfCodePoints(setOf('a'.toInt(), "\uD835\uDD38".codePointAt(0)))
      },
      object : AbstractGrammar<Int>() {
        override fun root() = noneOfCodePoints("a\uD835\uDD38")
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("b").apply {
          matched shouldBe true
          matchedEntireInput shouldBe true
          matchedInput shouldBe "b"
          restOfInput shouldBe ""
        }
        run("\uD835\uDD38").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "\uD835\uDD38"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = noneOfCodePoints("")
    }).apply {
      registerListener(IntegerTestListener())
      run("b").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "b"
        restOfInput shouldBe ""
      }
    }
  }
  "String rule grammar" {
    checkAll(Arb.string(1..100), Arb.string(0..100)) { prefix, suffix ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = string(prefix)
      }).apply {
        registerListener(IntegerTestListener())
        run("$prefix$suffix").apply {
          matched shouldBe true
          matchedEntireInput shouldBe suffix.isEmpty()
          matchedInput shouldBe prefix
          restOfInput shouldBe suffix
        }
      }
    }
    Arb.string(1..100).checkAll { suffix ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = string("")
      }).apply {
        registerListener(IntegerTestListener())
        run(suffix).apply {
          matched shouldBe true
          matchedEntireInput shouldBe suffix.isEmpty()
          matchedInput shouldBe ""
          restOfInput shouldBe suffix
        }
      }
    }
    checkAll(Arb.string(1..100), Arb.string(0..100)) { str, input ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = string(str)
      }).apply {
        registerListener(IntegerTestListener())
        val equals = str == input
        run(input).apply {
          matched shouldBe equals
          matchedEntireInput shouldBe equals
          matchedInput shouldBe if (equals) input else null
          restOfInput shouldBe if (equals) "" else input
        }
      }
    }
  }
  "IgnoreCase rule grammar" {
    checkAll(Arb.string(1..100), Arb.string(0..100)) { prefix, suffix ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = ignoreCase(prefix)
      }).apply {
        registerListener(IntegerTestListener())
        run("${prefix.toUpperCase()}$suffix").apply {
          matched shouldBe true
          matchedEntireInput shouldBe suffix.isEmpty()
          matchedInput shouldBe prefix.toUpperCase()
          restOfInput shouldBe suffix
        }
      }
    }
    Arb.string(1..100).checkAll { suffix ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = ignoreCase("")
      }).apply {
        registerListener(IntegerTestListener())
        run(suffix).apply {
          matched shouldBe true
          matchedEntireInput shouldBe suffix.isEmpty()
          matchedInput shouldBe ""
          restOfInput shouldBe suffix
        }
      }
    }
    checkAll(Arb.string(1..100), Arb.string(0..100)) { str, input ->
      Parser(object : AbstractGrammar<Int>() {
        override fun root() = ignoreCase(str)
      }).apply {
        registerListener(IntegerTestListener())
        val equals = str.equals(input, true)
        val upperCase = input.toUpperCase()
        run(upperCase).apply {
          matched shouldBe equals
          matchedEntireInput shouldBe equals
          matchedInput shouldBe if (equals) upperCase else null
          restOfInput shouldBe if (equals) "" else upperCase
        }
      }
    }
  }
})

@SuppressFBWarnings(
  value = ["SIC_INNER_SHOULD_BE_STATIC_ANON"],
  justification = "Performance is not of great importance in unit tests."
)
class AbstractGrammarTest {

  @Test
  fun regex_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return regex("abc+")
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcccccd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("abccccc", result.matchedInput)
    assertEquals("d", result.restOfInput)
  }

  @Test
  fun regex_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return string("abc+")
      }
    }

    val runner = Parser(Grammar())
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

    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          strings("football", "foo", "foobar"),
          command { ctx: RuleContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = Parser(Grammar())
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

    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          strings("foo"),
          command { ctx: RuleContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = Parser(Grammar())
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
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return strings("football", "foo", "foobar")
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fo", result.restOfInput)
  }

  @Test
  fun strings_invalid_set_empty() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return strings(setOf())
      }
    }

    val runner = Parser(Grammar())
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

    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          ignoreCase("football", "foo", "foobar"),
          command { ctx: RuleContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = Parser(Grammar())
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

    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          ignoreCase(setOf("foo")),
          command { ctx: RuleContext<Int> -> stringsRuleMatch.set(ctx.previousMatch) },
          string("baz")
        )
      }
    }

    val runner = Parser(Grammar())
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
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase("football", "foo", "foobar")
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ignoreCase_strings_invalid_set_empty() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return ignoreCase(setOf())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("fO")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("fO", result.restOfInput)
  }

  @Test
  fun ascii_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("#")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("#", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun ascii_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return ascii()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("ß", result.restOfInput)
  }

  @Test
  fun bmp_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ß")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("ß", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun bmp_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return bmp()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\uD835\uDD38")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\uD835\uDD38", result.restOfInput)
  }

  @Test
  fun digit_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("5")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("5", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun digit_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return digit()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("O")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("O", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("ä")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("ä", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierStart_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierStart()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("1", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("1", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun javaIdentifierPart_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return javaIdentifierPart()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("(")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("(", result.restOfInput)
  }

  @Test
  fun letter_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("Ü")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("Ü", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letter_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("$")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("$", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_letter() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("x")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("x", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_valid_digit() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return letterOrDigit()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("9")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("9", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun letterOrDigit_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return letter()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("%")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("%", result.restOfInput)
  }

  @Test
  fun printable_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun printable_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return printable()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun spaceChar_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run(" ")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals(" ", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun spaceChar_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return spaceChar()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun whitespace_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun whitespace_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return whitespace()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("_")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("_", result.restOfInput)
  }

  @Test
  fun cr_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\r", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun cr_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return cr()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n", result.restOfInput)
  }

  @Test
  fun lf_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun lf_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return lf()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\r", result.restOfInput)
  }

  @Test
  fun crlf_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\r\n")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("\r\n", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun crlf_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return crlf()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("\n\r")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("\n\r", result.restOfInput)
  }

  @Test
  fun sequence_valid_character() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          character('a'),
          character('b'),
          character('c')
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("abc", result.matchedInput)
    assertEquals("d", result.restOfInput)
  }

  @Test
  fun sequence_valid_empty() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("abcd")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("abcd", result.restOfInput)
  }

  @Test
  fun sequence_valid_push() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          push { ctx: RuleContext<Int> -> peek(ctx) + 4 },
          sequence(push { ctx: RuleContext<Int> -> pop(1, ctx) + peek(ctx) }),
          optional(action { ctx: RuleContext<Int> ->
            ctx.stack.push(0)
            false
          })
        )
      }
    }

    val runner = Parser(Grammar())
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
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          character('a'),
          character('b'),
          character('c')
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("acdc")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("acdc", result.restOfInput)
  }

  @Test
  fun firstOf_valid_sequence() {
    class Grammar : AbstractGrammar<Int>() {
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

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foobazxxx")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foobazxxx", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun firstOf_valid_empty() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return firstOf()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("foo", result.restOfInput)
  }

  @Test
  fun firstOf_valid_oneRule() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return firstOf(string("foo"))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("foo")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("foo", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun firstOf_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return firstOf(
          string("foo"),
          string("bar"),
          string("baz")
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("babafoo")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("babafoo", result.restOfInput)
  }

  @Test
  fun optional_valid_match() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun optional_valid_noMatch() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return optional(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_zero() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertTrue(result.matched)
    assertFalse(result.matchedEntireInput)
    assertEquals("", result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun zeroOrMore_valid_more() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return zeroOrMore(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("aaaaa", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_one() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("a")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("a", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_valid_more() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("aaaaa")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("aaaaa", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun oneOrMore_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return oneOrMore(character('a'))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("b")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("b", result.restOfInput)
  }

  @Test
  fun repeat_valid_times() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')) * 4
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_times() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(6, 7)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_range() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(4, 7)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_range() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).times(2, 4)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("z")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("z", result.restOfInput)
  }

  @Test
  fun repeat_valid_max() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).max(3)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_max() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return sequence(repeat(character('z')).max(3), eoi())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzz", result.restOfInput)
  }

  @Test
  fun repeat_valid_min() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(3)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("zzzzz", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun repeat_invalid_min() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return repeat(character('z')).min(8)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(CharSequenceTestListener())
    val result = runner.run("zzzzz")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("zzzzz", result.restOfInput)
  }

  @Test
  fun test_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          test(string("what")),
          string("whatever")
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("whatever", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun test_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          test(string("ever")),
          string("whatever")
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun testNot_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          testNot(string("foo")),
          string("whatever")
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("whatever", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun testNot_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          testNot(string("what")),
          string("whatever")
        )
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_withElse() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: RuleContext<Int> -> ctx.startIndex == 0 }, letter(), digit())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("z", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_then_noElse() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: RuleContext<Int> -> ctx.startIndex == 0 }, letter())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("z")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("z", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_valid_else() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: RuleContext<Int> -> ctx.startIndex != 0 }, letter(), digit())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("1")
    assertTrue(result.matched)
    assertTrue(result.matchedEntireInput)
    assertEquals("1", result.matchedInput)
    assertEquals("", result.restOfInput)
  }

  @Test
  fun conditional_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return conditional({ ctx: RuleContext<Int> -> ctx.startIndex == 0 }, never(), empty())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    val result = runner.run("whatever")
    assertFalse(result.matched)
    assertFalse(result.matchedEntireInput)
    assertNull(result.matchedInput)
    assertEquals("whatever", result.restOfInput)
  }

  @Test
  fun action_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: RuleContext<Int> ->
          ctx.stack.push(4711)
          assertEquals(0, ctx.level)
          assertNotNull(ctx.position)
          true
        }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_failingAction() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return action { ctx: RuleContext<Int> ->
          ctx.stack.push(4711)
          assertEquals(0, ctx.level)
          assertNotNull(ctx.position)
          false
        }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun action_invalid_illegalAdvanceIndex() {
    class EvilActionRule(action: Action<Int>) : ActionRule<Int>(action::run) {
      override fun match(context: ParserContext<Int>): Boolean {
        return super.match(context) && context.advanceIndex(1000)
      }
    }

    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return EvilActionRule { true }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertFalse(runner.run("whatever").matched)
  }

  @Test
  fun command_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return command { ctx: RuleContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_valid_noSkip() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return skippableAction { ctx: RuleContext<Int> ->
          ctx.stack.push(4711)
          true
        }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_valid_skip() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return test(
          skippableAction { ctx: RuleContext<Int> ->
            ctx.stack.push(4711)
            true
          })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun skippableAction_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return skippableAction { ctx: RuleContext<Int> ->
          ctx.stack.push(4711)
          false
        }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_noSkip() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return skippableCommand { ctx: RuleContext<Int> -> ctx.stack.push(4711) }
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun skippableCommand_valid_skip() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return test(
          skippableCommand { ctx: RuleContext<Int> -> ctx.stack.push(4711) })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun post_valid_suppliedEvent() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          string("whatever"),
          post { ctx: RuleContext<Int> -> ctx.previousMatch!! })
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

    val runner = Parser(Grammar())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    assertEquals("whatever", listener.string)
  }

  @Test
  fun post_valid_staticEvent() {
    class Grammar : AbstractGrammar<Int>() {
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

    val runner = Parser(Grammar())
    val listener = Listener()
    runner.registerListener(listener)
    runner.run("whatever")
    assertEquals("someEvent", listener.string)
  }

  @Test
  fun pop_valid_top() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), pop())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun pop_valid_down() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), pop(1))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_top() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke { 4712 })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(1, runner.run("whatever").stack.size)
  }

  @Test
  fun pop_valid_action() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          action { ctx: RuleContext<Int> -> pop(ctx) == 4711 })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_top() {
    class Grammar : AbstractGrammar<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          action { ctx: RuleContext<Number> ->
            pop(ctx) == 4711
          })
      }
    }

    val runner = Parser(Grammar())
    assertNull(runner.run("whatever").stackTop)
  }

  @Test
  fun popAs_valid_action_down() {
    class Grammar : AbstractGrammar<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: RuleContext<Number> ->
            pop(1, ctx) == 4711
          })
      }
    }

    val runner = Parser(Grammar())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun peek_valid_top() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), action { ctx: RuleContext<Int> ->
          peek(ctx) == 4711
        })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(4711, runner.run("whatever").stackTop)
  }

  @Test
  fun peek_valid_down() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: RuleContext<Int> -> peek(1, ctx) == 4711 })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_top() {
    class Grammar : AbstractGrammar<Number>() {
      override fun root(): Rule<Number> {
        return sequence(push(4711), action { ctx: RuleContext<Number> ->
          peek(ctx) == 4711
        })
      }
    }

    val runner = Parser(Grammar())
    assertEquals(4711, runner.run("whatever").stackTop)
  }

  @Test
  fun peekAs_valid_down() {
    class Grammar : AbstractGrammar<Number>() {
      override fun root(): Rule<Number> {
        return sequence(
          push(4711),
          push(4712),
          action { ctx: RuleContext<Number> ->
            peek(1, ctx) == 4711
          })
      }
    }

    val runner = Parser(Grammar())
    assertEquals(4712, runner.run("whatever").stackTop)
  }

  @Test
  fun poke_valid_staticValue_down() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1) { 4713 })
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_top() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), poke(4712))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(1, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_valid_suppliedValue_down() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), poke(1, 4713))
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stackTop)
    assertEquals(2, runner.run("whatever").stack.size)
  }

  @Test
  fun poke_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return poke(4712)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
  }

  @Test
  fun push_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return push(4711)
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
  }

  @Test
  fun dup_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), dup())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(2, runner.run("whatever").stack.size)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun dup_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return dup()
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
  }

  @Test
  fun swap_valid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), push(4712), swap())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertEquals(2, runner.run("whatever").stack.size)
    assertEquals(Integer.valueOf(4711), runner.run("whatever").stackTop)
    assertEquals(Integer.valueOf(4712), runner.run("whatever").stack.peek(1))
  }

  @Test
  fun swap_invalid() {
    class Grammar : AbstractGrammar<Int>() {
      override fun root(): Rule<Int> {
        return sequence(push(4711), swap())
      }
    }

    val runner = Parser(Grammar())
    runner.registerListener(IntegerTestListener())
    assertThrows(IndexOutOfBoundsException::class.java) { runner.run("whatever") }
  }

  @Test
  fun previousMatch_valid() {
    class Grammar : AbstractGrammar<CharSequence>() {
      override fun root(): Rule<CharSequence> {
        return sequence(
          string("hello"),
          string("world"),
          push { ctx: RuleContext<CharSequence> -> ctx.parent?.previousMatch!! },
          sequence(
            string("foo"),
            string("bar")
          ),
          push { ctx: RuleContext<CharSequence> -> ctx.parent?.previousMatch!! },
          test(string("baz")),
          push { ctx: RuleContext<CharSequence> -> ctx.parent?.previousMatch!! },
          sequence(
            test(string("ba")),
            string("b"),
            test(string("az"))
          ),
          push { ctx: RuleContext<CharSequence> -> ctx.parent?.previousMatch!! })
      }
    }

    val runner = Parser(Grammar())
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

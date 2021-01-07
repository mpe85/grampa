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
import com.mpe85.grampa.rule.impl.or
import com.mpe85.grampa.rule.impl.plus
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
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
        run(input).apply {
          matched shouldBe input.startsWith(str)
          matchedEntireInput shouldBe (str == input)
          matchedInput shouldBe if (input.startsWith(str)) str else null
          restOfInput shouldBe if (input.startsWith(str)) input.removePrefix(str) else input
        }
      }
    }
  }
  "StringIgnoreCase rule grammar" {
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
        run(input).apply {
          matched shouldBe input.toUpperCase().startsWith(str.toUpperCase())
          matchedEntireInput shouldBe (str == input)
          matchedInput?.toString()?.toUpperCase() shouldBe if (input.toUpperCase().startsWith(str.toUpperCase()))
            str.toUpperCase() else null
          restOfInput.toString().toUpperCase() shouldBe if (input.toUpperCase().startsWith(str.toUpperCase()))
            input.toUpperCase().removePrefix(str.toUpperCase()) else input.toUpperCase()
        }
      }
    }
  }
  "Regex rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = regex("abc+")
    }).apply {
      registerListener(IntegerTestListener())
      run("abcccccd").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "abccccc"
        restOfInput shouldBe "d"
      }
      run("ab").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "ab"
      }
    }
  }
  "Strings rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = strings("football", "foo", "foobar")
      },
      object : AbstractGrammar<Int>() {
        override fun root() = strings(setOf("foo"))
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("foobaz").apply {
          matched shouldBe true
          matchedEntireInput shouldBe false
          matchedInput shouldBe "foo"
          restOfInput shouldBe "baz"
        }
        run("fo").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "fo"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = strings(emptySet())
    }).apply {
      registerListener(IntegerTestListener())
      run("fO").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "fO"
      }
    }
  }
  "StringsIgnoreCase rule grammar" {
    listOf(
      object : AbstractGrammar<Int>() {
        override fun root() = ignoreCase("football", "foo", "foobar")
      },
      object : AbstractGrammar<Int>() {
        override fun root() = ignoreCase(setOf("foo"))
      }
    ).forAll { grammar ->
      Parser(grammar).apply {
        registerListener(IntegerTestListener())
        run("fOObaz").apply {
          matched shouldBe true
          matchedEntireInput shouldBe false
          matchedInput shouldBe "fOO"
          restOfInput shouldBe "baz"
        }
        run("fO").apply {
          matched shouldBe false
          matchedEntireInput shouldBe false
          matchedInput shouldBe null
          restOfInput shouldBe "fO"
        }
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = ignoreCase(emptySet())
    }).apply {
      registerListener(IntegerTestListener())
      run("fO").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "fO"
      }
    }
  }
  "ASCII rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = ascii()
    }).apply {
      registerListener(IntegerTestListener())
      run("#").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "#"
        restOfInput shouldBe ""
      }
      run("ß").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "ß"
      }
    }
  }
  "BMP rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = bmp()
    }).apply {
      registerListener(IntegerTestListener())
      run("ß").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "ß"
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
  "Digit rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = digit()
    }).apply {
      registerListener(IntegerTestListener())
      run("5").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "5"
        restOfInput shouldBe ""
      }
      run("O").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "O"
      }
    }
  }
  "JavaIdentifierStart rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = javaIdentifierStart()
    }).apply {
      registerListener(IntegerTestListener())
      run("ä").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "ä"
        restOfInput shouldBe ""
      }
      run("1").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "1"
      }
    }
  }
  "JavaIdentifierPart rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = javaIdentifierPart()
    }).apply {
      registerListener(IntegerTestListener())
      run("1").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "1"
        restOfInput shouldBe ""
      }
      run("(").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "("
      }
    }
  }
  "Letter rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = letter()
    }).apply {
      registerListener(IntegerTestListener())
      run("Ü").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "Ü"
        restOfInput shouldBe ""
      }
      run("$").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "$"
      }
    }
  }
  "LetterOrDigit rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = letterOrDigit()
    }).apply {
      registerListener(IntegerTestListener())
      run("x").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "x"
        restOfInput shouldBe ""
      }
      run("9").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "9"
        restOfInput shouldBe ""
      }
      run("%").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "%"
      }
    }
  }
  "Printable rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = printable()
    }).apply {
      registerListener(IntegerTestListener())
      run("n").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "n"
        restOfInput shouldBe ""
      }
      run("\n").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\n"
      }
    }
  }
  "SpaceChar rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = spaceChar()
    }).apply {
      registerListener(IntegerTestListener())
      run(" ").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe " "
        restOfInput shouldBe ""
      }
      run("\n").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\n"
      }
    }
  }
  "Whitespace rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = whitespace()
    }).apply {
      registerListener(IntegerTestListener())
      run("\n").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\n"
        restOfInput shouldBe ""
      }
      run("_").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "_"
      }
    }
  }
  "CR rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = cr()
    }).apply {
      registerListener(IntegerTestListener())
      run("\r").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\r"
        restOfInput shouldBe ""
      }
      run("\n").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\n"
      }
    }
  }
  "LF rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = lf()
    }).apply {
      registerListener(IntegerTestListener())
      run("\n").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\n"
        restOfInput shouldBe ""
      }
      run("\r").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\r"
      }
    }
  }
  "CRLF rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = crlf()
    }).apply {
      registerListener(IntegerTestListener())
      run("\r\n").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "\r\n"
        restOfInput shouldBe ""
      }
      run("\n\r").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "\n\r"
      }
    }
  }
  "Sequence rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = character('a') + character('b') + character('c')
    }).apply {
      registerListener(IntegerTestListener())
      run("abcd").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "abc"
        restOfInput shouldBe "d"
      }
      run("acdc").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "acdc"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = sequence()
    }).apply {
      registerListener(IntegerTestListener())
      run("abcd").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe ""
        restOfInput shouldBe "abcd"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = sequence(
        push(4711),
        push { peek(it) + 4 },
        sequence(push { pop(1, it) + peek(it) }),
        optional(action {
          it.stack.push(0)
          false
        })
      )
    }).apply {
      registerListener(IntegerTestListener())
      run("whatever").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe ""
        restOfInput shouldBe "whatever"
        stackTop shouldBe 9426
        stack.size shouldBe 2
        stack.peek() shouldBe 9426
        stack.peek(1) shouldBe 4715
      }
    }
  }
  "FirstOf rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = firstOf(
        string("foo") + string("bar"),
        string("foo") + string("baz")
      ) + string("xxx")
    }).apply {
      registerListener(IntegerTestListener())
      run("foobazxxx").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "foobazxxx"
        restOfInput shouldBe ""
      }
      run("foobar").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "foobar"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = firstOf()
    }).apply {
      registerListener(IntegerTestListener())
      run("foo").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe ""
        restOfInput shouldBe "foo"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = firstOf(string("foo"))
    }).apply {
      registerListener(IntegerTestListener())
      run("foo").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "foo"
        restOfInput shouldBe ""
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = string("foo") or
          string("bar") or
          string("baz")
    }).apply {
      registerListener(IntegerTestListener())
      run("bamboo").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "bamboo"
      }
    }
  }
  "Optional rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = optional(character('a'))
    }).apply {
      registerListener(IntegerTestListener())
      run("a").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "a"
        restOfInput shouldBe ""
      }
      run("b").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe ""
        restOfInput shouldBe "b"
      }
    }
  }
  "ZeroOrMore rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = zeroOrMore(character('a'))
    }).apply {
      registerListener(IntegerTestListener())
      run("b").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe ""
        restOfInput shouldBe "b"
      }
      run("ab").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "a"
        restOfInput shouldBe "b"
      }
      run("aaaaa").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "aaaaa"
        restOfInput shouldBe ""
      }
    }
  }
  "OneOrMore rule grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = oneOrMore(character('a'))
    }).apply {
      registerListener(IntegerTestListener())
      run("b").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "b"
      }
      run("ab").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "a"
        restOfInput shouldBe "b"
      }
      run("aaaaa").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "aaaaa"
        restOfInput shouldBe ""
      }
    }
  }
  "Repeat rul grammar" {
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = repeat(character('z')) * 4
    }).apply {
      registerListener(IntegerTestListener())
      run("zzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "zzzz"
        restOfInput shouldBe ""
      }
      run("zzzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "zzzz"
        restOfInput shouldBe "z"
      }
      run("zzz").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "zzz"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = repeat(character('z')).times(4, 7)
    }).apply {
      registerListener(IntegerTestListener())
      run("zzzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "zzzzz"
        restOfInput shouldBe ""
      }
      run("zzzzzzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "zzzzzzz"
        restOfInput shouldBe "z"
      }
      run("zzz").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "zzz"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = repeat(character('z')).max(3)
    }).apply {
      registerListener(IntegerTestListener())
      run("zz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "zz"
        restOfInput shouldBe ""
      }
      run("zzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe false
        matchedInput shouldBe "zzz"
        restOfInput shouldBe "z"
      }
    }
    Parser(object : AbstractGrammar<Int>() {
      override fun root() = repeat(character('z')).min(3)
    }).apply {
      registerListener(IntegerTestListener())
      run("zzzzz").apply {
        matched shouldBe true
        matchedEntireInput shouldBe true
        matchedInput shouldBe "zzzzz"
        restOfInput shouldBe ""
      }
      run("z").apply {
        matched shouldBe false
        matchedEntireInput shouldBe false
        matchedInput shouldBe null
        restOfInput shouldBe "z"
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
    assertThrows(NoSuchElementException::class.java) { runner.run("whatever") }
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

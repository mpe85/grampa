package com.mpe85.grampa.grammar

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.ActionRule
import com.mpe85.grampa.rule.impl.or
import com.mpe85.grampa.rule.impl.plus
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.greenrobot.eventbus.Subscribe

private class IntegerTestListener : ParseEventListener<Int>()
private class CharSequenceTestListener : ParseEventListener<CharSequence>()

class AbstractGrammarTests : StringSpec({
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
            override fun root() = char('a') + char('b') + char('c')
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
            override fun root() = optional(char('a'))
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
            override fun root() = zeroOrMore(char('a'))
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
            override fun root() = oneOrMore(char('a'))
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
            override fun root() = repeat(char('z'), 4)
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
            override fun root() = repeat(char('z'), 4, 7)
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
            override fun root() = repeat(char('z'), max = 3)
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
            override fun root() = repeat(char('z'), min = 3)
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
    "Test rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = test(string("what")) + string("whatever")
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "whatever"
                restOfInput shouldBe ""
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = test(string("ever")) + string("whatever")
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe "whatever"
            }
        }
    }
    "TestNot rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = testNot(string("foo")) + string("whatever")
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "whatever"
                restOfInput shouldBe ""
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = testNot(string("what")) + string("whatever")
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe "whatever"
            }
        }
    }
    "Conditional rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = conditional({ it.startIndex == 0 }, letter(), digit())
        }).apply {
            registerListener(IntegerTestListener())
            run("z").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "z"
                restOfInput shouldBe ""
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = conditional({ it.startIndex == 0 }, letter())
        }).apply {
            registerListener(IntegerTestListener())
            run("z").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "z"
                restOfInput shouldBe ""
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = conditional({ it.startIndex != 0 }, letter(), digit())
        }).apply {
            registerListener(IntegerTestListener())
            run("1").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "1"
                restOfInput shouldBe ""
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = conditional({ it.startIndex == 0 }, never(), empty())
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe "whatever"
            }
        }
    }
    "Action rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = action {
                it.stack.push(4711)
                it.level shouldBe 0
                it.position shouldNotBe null
                true
            }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = action {
                it.stack.push(4711)
                it.level shouldBe 0
                it.position shouldNotBe null
                false
            }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = object : ActionRule<Int>({ true }) {
                override fun match(context: ParserContext<Int>) = super.match(context) && context.advanceIndex(1000)
            }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").matched shouldBe false
        }
    }
    "Command rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = command { it.stack.push(4711) }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
    }
    "SkippableAction rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = skippableAction {
                it.stack.push(4711)
                true
            }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = test(skippableAction {
                it.stack.push(4711)
                true
            })
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = skippableAction {
                it.stack.push(4711)
                false
            }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
    }
    "SkippableCommand rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = skippableCommand { it.stack.push(4711) }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = test(skippableCommand { it.stack.push(4711) })
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
    }
    "Post rule grammar" {
        class Listener : ParseEventListener<Int>() {
            var string: String? = null
                private set

            @Subscribe
            @SuppressFBWarnings("UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS")
            fun stringEvent(event: String) {
                string = event
            }

            override fun afterMatchSuccess(event: MatchSuccessEvent<Int>) = event.context shouldNotBe null
            override fun afterParse(event: PostParseEvent<Int>) = event.result shouldNotBe null
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = string("whatever") + post { it.previousMatch!! }
        }).apply {
            Listener().let { listener ->
                registerListener(listener)
                run("whatever")
                listener.string shouldBe "whatever"
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = string("whatever") + post("someEvent")
        }).apply {
            Listener().let { listener ->
                registerListener(listener)
                run("whatever")
                listener.string shouldBe "someEvent"
            }
        }
    }
    "Push rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711)
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
    }
    "Pop rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = sequence(push(4711), pop())
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = sequence(push(4711), push(4712), pop(1))
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4712
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + action { pop(it) == 4711 }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe null
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + push(4712) + action { pop(1, it) == 4711 }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4712
        }
    }
    "Poke rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = sequence(push(4711), poke { 4712 })
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stackTop shouldBe 4712
                stack.size shouldBe 1
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + push(4712) + poke(1) { 4713 }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stackTop shouldBe 4712
                stack.size shouldBe 2
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + poke(4712)
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stackTop shouldBe 4712
                stack.size shouldBe 1
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + push(4712) + poke(1, 4713)
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stackTop shouldBe 4712
                stack.size shouldBe 2
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = poke(4712)
        }).apply {
            registerListener(IntegerTestListener())
            shouldThrow<IndexOutOfBoundsException> { run("whatever") }
        }
    }
    "Peek rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + action { peek(it) == 4711 }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4711
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + push(4712) + action { peek(1, it) == 4711 }
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").stackTop shouldBe 4712
        }
    }
    "Dup rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + dup()
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stack.size shouldBe 2
                stackTop shouldBe 4711
                stack.peek(1) shouldBe 4711
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = dup()
        }).apply {
            registerListener(IntegerTestListener())
            shouldThrow<NoSuchElementException> { run("whatever") }
        }
    }
    "Swap rule grammar" {
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + push(4712) + swap()
        }).apply {
            registerListener(IntegerTestListener())
            run("whatever").apply {
                stack.size shouldBe 2
                stackTop shouldBe 4711
                stack.peek(1) shouldBe 4712
            }
        }
        Parser(object : AbstractGrammar<Int>() {
            override fun root() = push(4711) + swap()
        }).apply {
            registerListener(IntegerTestListener())
            shouldThrow<IndexOutOfBoundsException> { run("whatever") }
        }
    }
    "Previous match" {
        Parser(object : AbstractGrammar<CharSequence>() {
            override fun root() = sequence(
                string("hello"),
                string("world"),
                push { it.parent?.previousMatch!! },
                string("foo") + string("bar"),
                push { it.parent?.previousMatch!! },
                test(string("baz")),
                push { it.parent?.previousMatch!! },
                test(string("ba")) + string("b") + test(string("az")),
                push { it.parent?.previousMatch!! })
        }).apply {
            registerListener(CharSequenceTestListener())
            run("helloworldfoobarbaz").apply {
                matched shouldBe true
                matchedEntireInput shouldBe false
                stack.pop() shouldBe "b"
                stack.pop() shouldBe "foobar"
                stack.pop() shouldBe "foobar"
                stackTop shouldBe "world"
            }
        }
    }
})

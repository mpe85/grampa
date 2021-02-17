package com.mpe85.grampa.grammar

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.ActionRule
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
    "Repeat rul grammar" {
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

package com.mpe85.grampa.grammar

import com.mpe85.grampa.event.MatchSuccessEvent
import com.mpe85.grampa.event.ParseEventListener
import com.mpe85.grampa.event.PostParseEvent
import com.mpe85.grampa.parser.Parser
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

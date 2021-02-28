package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.plus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class PeekRuleTests : StringSpec({
    "Peek(context) rule peeks value at top of parser stack" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int) + command { peek(it) shouldBe int }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int
                stack.size shouldBe 1
            }
        }
    }
    "Peek(down,context) rule peeks value on parser stack at given down position" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + push(int2) + command { peek(1, it) shouldBe int1 }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 2
            }
        }
    }
    "Peek(value) throws NoSuchElementException when stack is empty" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = command { peek(it) }
        }).apply {
            shouldThrow<NoSuchElementException> { run("") }
        }
    }
})

package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.plus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class SwapRuleTests : StringSpec({
    "Swap rule swaps values on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + push(int2) + swap()
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int1
                stack.size shouldBe 2
                stack.peek(1) shouldBe int2
            }
        }
    }
    "Swap rule throws IndexOutOfBoundsException when stack is empty" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = swap()
        }).apply {
            shouldThrow<IndexOutOfBoundsException> { run("") }
        }
    }
    "Swap rule throws IndexOutOfBoundsException when stack has one element only" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int) + swap()
            }).apply {
                shouldThrow<IndexOutOfBoundsException> { run("") }
            }
        }
    }
})

package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.plus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class DupRuleTests : StringSpec({
    "Dup rule duplicates value on parser stack" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int) + dup()
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int
                stack.size shouldBe 2
                stack.peek(1) shouldBe int
            }
        }
    }
    "Dup rule throws NoSuchElementException when stack is empty" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = dup()
        }).apply {
            shouldThrow<NoSuchElementException> { run("") }
        }
    }
})

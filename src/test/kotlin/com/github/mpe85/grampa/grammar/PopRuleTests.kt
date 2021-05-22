package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.plus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class PopRuleTests : StringSpec({
    "Pop rule pops value from parser stack" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int) + pop()
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe null
                stack.size shouldBe 0
            }
        }
    }
    "Pop(down) rule pops value from parser stack at given down position" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int1) + push(int2) + pop(1)
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 1
            }
        }
    }
    "Pop(context) rule pops value from parser stack" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int) + command { pop(it) shouldBe int }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe null
                stack.size shouldBe 0
            }
        }
    }
    "Pop(down,context) rule pops value from parser stack at given down position" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int1) + push(int2) + command { pop(1, it) shouldBe int1 }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 1
            }
        }
    }
    "Pop(value) throws NoSuchElementException when stack is empty" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = pop()
        }).apply {
            shouldThrow<NoSuchElementException> { run("") }
        }
    }
})

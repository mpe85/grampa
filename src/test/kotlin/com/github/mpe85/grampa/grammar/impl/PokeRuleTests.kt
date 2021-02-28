package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.plus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class PokeRuleTests : StringSpec({
    "Poke(value) rule replaces value on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + poke(int2)
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
    "Poke(supplier) rule replaces value on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + poke { int2 }
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
    "Poke(down,value) rule replaces value on parser stack at given down position" {
        checkAll<Int, Int, Int> { int1, int2, int3 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + push(int2) + poke(1, int3)
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 2
                stack.peek(1) shouldBe int3
            }
        }
    }
    "Poke(down,supplier) rule replaces value on parser stack at given down position" {
        checkAll<Int, Int, Int> { int1, int2, int3 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + push(int2) + poke(1) { int3 }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 2
                stack.peek(1) shouldBe int3
            }
        }
    }
    "Poke(context,value) rule replaces value on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + command { poke(it, int2) }
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
    "Poke(context,down,value) rule replaces value on parser stack at given down position" {
        checkAll<Int, Int, Int> { int1, int2, int3 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int1) + push(int2) + command { poke(1, it, int3) }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int2
                stack.size shouldBe 2
                stack.peek(1) shouldBe int3
            }
        }
    }
    "Poke(value) throws IndexOutOfBoundsException when stack is empty" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = poke(int)
            }).apply {
                shouldThrow<IndexOutOfBoundsException> { run("") }
            }
        }
    }
})

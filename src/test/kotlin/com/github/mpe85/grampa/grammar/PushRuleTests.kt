package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class PushRuleTests : StringSpec({
    "Push rule pushes value on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push(int1) + push(1, int2)
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int1
                stack.peek(1) shouldBe int2
            }
        }
    }
    "Push rule pushes supplied value on parser stack" {
        checkAll<Int, Int> { int1, int2 ->
            Parser(object : AbstractGrammar<Int>() {
                override fun start() = push { int1 } + push(1) { int2 }
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int1
                stack.peek(1) shouldBe int2
            }
        }
    }
})

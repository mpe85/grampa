package com.mpe85.grampa.grammar.impl

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.lowerCaseCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.plus
import com.mpe85.grampa.rule.impl.toRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class TestNotRuleTests : StringSpec({
    "TestNot rule matches failing rule" {
        checkAll(Arb.string(2..10, lowerCaseCodePoints()), Arb.string(2..10, lowerCaseCodePoints())) { lower, upper ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = testNot(lower.toRule()) + upper.toRule()
            }).run(upper).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe upper
                restOfInput shouldBe ""
            }
        }
    }
    "TestNot rule does not match matching rule" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = testNot(str.toRule()) + str.toRule()
            }).run(str).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe str
            }
        }
    }
})

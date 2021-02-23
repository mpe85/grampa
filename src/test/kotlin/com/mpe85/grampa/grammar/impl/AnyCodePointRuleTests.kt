package com.mpe85.grampa.grammar.impl

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class AnyCodePointRuleTests : StringSpec({
    "AnyCodePoint rule matches all code points" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = anyCodePoint()
        }).apply {
            checkAll(Arb.string(1, legalCodePoints())) { str ->
                run(str).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe str
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "AnyCodePoint rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = anyCodePoint()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

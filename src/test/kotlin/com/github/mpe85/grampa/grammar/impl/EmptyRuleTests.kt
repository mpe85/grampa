package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EmptyRuleTests : StringSpec({
    "Empty rule matches non-empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = empty()
        }).apply {
            checkAll(Arb.string(1, 10, legalCodePoints())) { str ->
                run(str).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe ""
                    restOfInput shouldBe str
                }
            }
        }
    }
    "Empty rule matches empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = empty()
        }).run("").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe ""
            restOfInput shouldBe ""
        }
    }
})

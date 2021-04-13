package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EoiRuleTests : StringSpec({
    "EOI rule matches at end of any input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = sequence(zeroOrMore(anyCodePoint()), eoi())
        }).apply {
            checkAll(Arb.string(0, 10, legalCodePoints())) { str ->
                run(str).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe str
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "EOI rule does not match before end of input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = eoi()
        }).apply {
            checkAll(Arb.string(1, 10, legalCodePoints())) { str ->
                run(str).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe str
                }
            }
        }
    }
})

package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class NeverRuleTests : StringSpec({
    "Never rule matches no input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = never()
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

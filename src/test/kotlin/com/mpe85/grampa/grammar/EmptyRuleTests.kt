package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.codepoints
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class EmptyRuleTests : StringSpec({
    "Empty rule matches non-empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = empty()
        }).apply {
            checkAll(Arb.string(1, Arb.codepoints())) { str ->
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
            override fun root() = empty()
        }).apply {
            run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
            }
        }
    }
})

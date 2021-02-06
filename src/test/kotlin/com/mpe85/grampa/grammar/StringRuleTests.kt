package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class StringRuleTests : StringSpec({
    "String rule matches correct string" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = string(str)
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "String rule does not match wrong string" {
        checkAll(Arb.string(1..10, legalCodePoints()), Arb.string(1..10, legalCodePoints())) { str1, str2 ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = string(str1)
            }).run(str2).apply {
                matched shouldBe str2.startsWith(str1)
                matchedEntireInput shouldBe (str1 == str2)
                matchedInput shouldBe if (str2.startsWith(str1)) str1 else null
                restOfInput shouldBe if (str2.startsWith(str1)) str2.drop(str1.length) else str2
            }
        }
    }
    "Empty String rule matches any input" {
        checkAll(Arb.string(0..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = string("")
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe str.isEmpty()
                matchedInput shouldBe ""
                restOfInput shouldBe str
            }
        }
    }
})

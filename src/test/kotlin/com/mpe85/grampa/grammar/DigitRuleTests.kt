package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter
import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class DigitRuleTests : StringSpec({
    "Digit rule matches all digit characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = digit()
        }).apply {
            checkAll(legalCodePoints().filter { UCharacter.isDigit(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Digit rule does not match non-digit characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = digit()
        }).apply {
            checkAll(legalCodePoints().filterNot { UCharacter.isDigit(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Digit rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = digit()
        }).apply {
            run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

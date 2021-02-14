package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class CrRuleTests : StringSpec({
    "CR rule matches CR character" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = cr()
        }).run("\r").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe "\r"
            restOfInput shouldBe ""
        }
    }
    "CR rule does not match non-CR codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = cr()
        }).apply {
            checkAll(legalCodePoints().filterNot { it.value == '\r'.toInt() }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "CR rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = cr()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

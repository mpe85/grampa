package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter.isPrintable
import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class PrintableRuleTests : StringSpec({
    "Printable rule matches all printable codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = printable()
        }).apply {
            checkAll(legalCodePoints().filter { isPrintable(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Printable rule does not match non-printable codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = printable()
        }).apply {
            checkAll(legalCodePoints().filterNot { isPrintable(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Printable rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = printable()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

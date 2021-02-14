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

class WhitespaceRuleTests : StringSpec({
    "Whitespace rule matches all whitespace codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = whitespace()
        }).apply {
            checkAll(legalCodePoints().filter { UCharacter.isWhitespace(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Whitespace rule does not match non-whitespace codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = whitespace()
        }).apply {
            checkAll(legalCodePoints().filterNot { UCharacter.isWhitespace(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Whitespace rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = whitespace()
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

package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.isBMP
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class BmpRuleTests : StringSpec({
    "Bmp rule matches all BMP codepoints" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = bmp()
        }).apply {
            checkAll(legalCodePoints().filter { isBMP(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Bmp rule does not match non-BMP codepoints" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = bmp()
        }).apply {
            checkAll(legalCodePoints().filterNot { isBMP(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Bmp rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = bmp()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

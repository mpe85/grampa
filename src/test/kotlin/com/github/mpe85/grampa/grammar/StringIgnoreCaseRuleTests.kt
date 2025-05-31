package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.lang.UCharacter.toUpperCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arabic
import io.kotest.property.arbitrary.cyrillic
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class StringIgnoreCaseRuleTests : StringSpec({
    "StringIgnoreCase rule matches correct string" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(str)
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule matches correct lowercase string" {
        checkAll(Arb.string(1..10, upperCaseCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(str)
            }).run(toLowerCase(str)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toLowerCase(str)
                restOfInput shouldBe ""
            }
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(toLowerCase(str))
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule matches correct uppercase string" {
        checkAll(Arb.string(1..10, lowerCaseCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(str)
            }).run(toUpperCase(str)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toUpperCase(str)
                restOfInput shouldBe ""
            }
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(toUpperCase(str))
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule does not match wrong string" {
        checkAll(Arb.string(1..10, Codepoint.arabic()), Arb.string(1..10, Codepoint.cyrillic())) { str1, str2 ->
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase(str1)
            }).run(str2).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe str2
            }
        }
    }
    "Empty StringIgnoreCase rule matches any input" {
        checkAll(Arb.string(0..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
                override fun start() = stringIgnoreCase("")
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe str.isEmpty()
                matchedInput shouldBe ""
                restOfInput shouldBe str
            }
        }
    }
})

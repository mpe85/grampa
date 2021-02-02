package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.lowerCaseCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.upperCaseCodePoints
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class CodePointRuleTests : StringSpec({
    "CodePoint rule matches correct character" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePoint(cp.value)
            }).run(String.format("%c", cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe String.format("%c", cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase CodePoint rule does not match uppercase character" {
        checkAll(lowerCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePoint(cp.value)
            }).apply {
                val upperCase = UCharacter.toUpperCase(cp.value)
                run(String.format("%c", upperCase)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe String.format("%c", upperCase)
                }
            }
        }
    }
    "Uppercase CodePoint rule does not match lowercase character" {
        checkAll(upperCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePoint(cp.value)
            }).apply {
                val lowerCase = UCharacter.toLowerCase(cp.value)
                run(String.format("%c", lowerCase)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe String.format("%c", lowerCase)
                }
            }
        }
    }
    "CodePoint rule does not match wrong character" {
        checkAll(Arb.set(legalCodePoints(), 2..2)) { codePoints ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePoint(codePoints.first().value)
            }).run(String.format("%c", codePoints.last().value)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe String.format("%c", codePoints.last().value)
            }
        }
    }
    "CodePoint rule does not match empty input" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePoint(cp.value)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

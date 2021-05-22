package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class CodePointRuleTests : StringSpec({
    "CodePoint rule matches correct codepoint" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = codePoint(cp.value)
            }).run(toString(cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toString(cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase CodePoint rule does not match uppercase codepoint" {
        checkAll(lowerCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = codePoint(cp.value)
            }).apply {
                val upperCase = UCharacter.toUpperCase(cp.value)
                run(toString(upperCase)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(upperCase)
                }
            }
        }
    }
    "Uppercase CodePoint rule does not match lowercase codepoint" {
        checkAll(upperCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = codePoint(cp.value)
            }).apply {
                val lowerCase = UCharacter.toLowerCase(cp.value)
                run(toString(lowerCase)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(lowerCase)
                }
            }
        }
    }
    "CodePoint rule does not match wrong codepoint" {
        checkAll(Arb.set(legalCodePoints(), 2..2)) { codePoints ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = codePoint(codePoints.first().value)
            }).run(toString(codePoints.last().value)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe toString(codePoints.last().value)
            }
        }
    }
    "CodePoint rule does not match empty input" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = codePoint(cp.value)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

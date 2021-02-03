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

class CodePointIgnoreCaseRuleTests : StringSpec({
    "CodePointIgnoreCase rule matches correct codepoint" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(cp.value)
            }).run(String.format("%c", cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe String.format("%c", cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase CodePointIgnoreCase rule matches uppercase codepoint" {
        checkAll(lowerCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(cp.value)
            }).apply {
                val upperCase = UCharacter.toUpperCase(cp.value)
                run(String.format("%c", upperCase)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe String.format("%c", upperCase)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Uppercase CodePointIgnoreCase rule matches lowercase codepoint" {
        checkAll(upperCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(cp.value)
            }).apply {
                val lowerCase = UCharacter.toLowerCase(cp.value)
                run(String.format("%c", lowerCase)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe String.format("%c", lowerCase)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "CodePointIgnoreCase rule does not match wrong codepoint" {
        checkAll(Arb.set(legalCodePoints(), 2..2)) { codePoints ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(codePoints.first().value)
            }).run(String.format("%c", codePoints.last().value)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe String.format("%c", codePoints.last().value)
            }
        }
    }
    "CodePointIgnoreCase rule does not match empty input" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(cp.value)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

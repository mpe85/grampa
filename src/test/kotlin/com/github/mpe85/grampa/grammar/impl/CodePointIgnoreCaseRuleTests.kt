package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.lang.UCharacter.toString
import com.ibm.icu.lang.UCharacter.toUpperCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class CodePointIgnoreCaseRuleTests : StringSpec({
    "CodePointIgnoreCase rule matches correct codepoint" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(cp.value)
            }).run(toString(cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toString(cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase CodePointIgnoreCase rule matches uppercase codepoint" {
        checkAll(lowerCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(cp.value)
            }).apply {
                val upperCase = toUpperCase(cp.value)
                run(toString(upperCase)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(upperCase)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Uppercase CodePointIgnoreCase rule matches lowercase codepoint" {
        checkAll(upperCaseCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(cp.value)
            }).apply {
                val lowerCase = toLowerCase(cp.value)
                run(toString(lowerCase)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(lowerCase)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "CodePointIgnoreCase rule does not match wrong codepoint" {
        checkAll(Arb.set(legalCodePoints(), 2..2)) { codePoints ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(codePoints.first().value)
            }).run(toString(codePoints.last().value)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe toString(codePoints.last().value)
            }
        }
    }
    "CodePointIgnoreCase rule does not match empty input" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(cp.value)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class LetterOrDigitRuleTests : StringSpec({
    "LetterOrDigit rule matches all letter or digit characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = letterOrDigit()
        }).apply {
            checkAll(Arb.char().filter { Character.isLetterOrDigit(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe ch.toString()
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "LetterOrDigit rule matches all letter or digit codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = letterOrDigit()
        }).apply {
            checkAll(legalCodePoints().filter { UCharacter.isLetterOrDigit(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "LetterOrDigit rule does not match non letter or digit characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = letterOrDigit()
        }).apply {
            checkAll(Arb.char().filterNot { Character.isLetterOrDigit(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe ch.toString()
                }
            }
        }
    }
    "LetterOrDigit rule does not match non letter or digit codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = letterOrDigit()
        }).apply {
            checkAll(legalCodePoints().filterNot { UCharacter.isLetterOrDigit(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "LetterOrDigit rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = letterOrDigit()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

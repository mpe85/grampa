package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class StringIgnoreCaseRuleTests : StringSpec({
    "StringIgnoreCase rule matches correct string" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str)
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule matches correct lowercase string" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str)
            }).run(str.toLowerCase()).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str.toLowerCase()
                restOfInput shouldBe ""
            }
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str.toLowerCase())
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule matches correct uppercase string" {
        checkAll(Arb.string(1..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str)
            }).run(str.toUpperCase()).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str.toUpperCase()
                restOfInput shouldBe ""
            }
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str.toUpperCase())
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
    "StringIgnoreCase rule does not match wrong string" {
        checkAll(Arb.string(1..10, legalCodePoints()), Arb.string(1..10, legalCodePoints())) { str1, str2 ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase(str1)
            }).run(str2).apply {
                matched shouldBe str2.startsWith(str1, true)
                matchedEntireInput shouldBe str1.equals(str2, true)
                matchedInput shouldBe if (str2.startsWith(str1, true)) str1 else null
                restOfInput shouldBe if (str2.startsWith(str1, true)) str2.drop(str1.length) else str2
            }
        }
    }
    "Empty StringIgnoreCase rule matches any input" {
        checkAll(Arb.string(0..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = ignoreCase("")
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe str.isEmpty()
                matchedInput shouldBe ""
                restOfInput shouldBe str
            }
        }
    }
})

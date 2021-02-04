package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class CodePointRangeRuleTests : StringSpec({
    "CodePointRange rule matches codepoint in range" {
        checkAll(legalCodePoints(), legalCodePoints(), legalCodePoints()) { cp1, cp2, cp3 ->
            val codePoints = listOf(cp1.value, cp2.value, cp3.value).sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePointRange(codePoints[0], codePoints[2])
            }).run(String.format("%c", codePoints[1])).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe String.format("%c", codePoints[1])
                restOfInput shouldBe ""
            }
        }
    }
    "CodePointRange rule does not match codepoint below of range" {
        checkAll(Arb.set(legalCodePoints(), 3..3)) { codepoints ->
            val (below, lower, upper) = codepoints.toList().map { it.value }.sorted()
                .let { Triple(it[0], it[1], it[2]) }
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePointRange(lower, upper)
            }).run(String.format("%c", below)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe String.format("%c", below)
            }
        }
    }
    "CodePointRange rule does not match codepoint above of range" {
        checkAll(Arb.set(legalCodePoints(), 3..3)) { codepoints ->
            val (lower, upper, above) = codepoints.toList().map { it.value }.sorted()
                .let { Triple(it[0], it[1], it[2]) }
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePointRange(lower, upper)
            }).run(String.format("%c", above)).apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe String.format("%c", above)
            }
        }
    }
    "CodePointRange rule does not match empty input" {
        checkAll(legalCodePoints(), legalCodePoints()) { cp1, cp2 ->
            val codePoints = listOf(cp1.value, cp2.value).sorted()
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = codePointRange(codePoints[0], codePoints[1])
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
    "CodePointRange rule throws exception when lower bound is higher than higher bound" {
        checkAll(Arb.set(legalCodePoints(), 2..2)) { codePoints ->
            val (low, high) = codePoints.toList().map { it.value }.sorted().let { it[0] to it[1] }
            shouldThrow<IllegalArgumentException> {
                Parser(object : AbstractGrammar<Unit>() {
                    override fun root() = codePointRange(high, low)
                })
            }
        }
    }
})

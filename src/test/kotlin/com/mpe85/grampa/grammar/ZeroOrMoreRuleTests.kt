package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.lowerCaseCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.toRule
import com.mpe85.grampa.upperCaseCodePoints
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll

class ZeroOrMoreRuleTests : StringSpec({
    "ZeroOrMore rule matches if child rule matches once" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = zeroOrMore(cp.value.toRule())
            }).run(toString(cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toString(cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "ZeroOrMore rule matches if child rule matches multiple times" {
        checkAll(legalCodePoints(), Arb.positiveInts(10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = zeroOrMore(cp.value.toRule())
            }).apply {
                val repeated = toString(cp.value).repeat(n)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe repeated
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "ZeroOrMore rule matches if child rule does not match" {
        checkAll(lowerCaseCodePoints(), upperCaseCodePoints()) { lower, upper ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = zeroOrMore(lower.value.toRule())
            }).apply {
                run(toString(upper.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe ""
                    restOfInput shouldBe toString(upper.value)
                }
            }
        }
    }
})

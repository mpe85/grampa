package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.toRule
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll

class OneOrMoreRuleTests : StringSpec({
    "OneOrMore rule matches if child rule matches once" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = oneOrMore(cp.value.toRule())
            }).run(toString(cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toString(cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "OneOrMore rule matches if child rule matches multiple times" {
        checkAll(legalCodePoints(), Arb.positiveInts(10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = oneOrMore(cp.value.toRule())
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
    "OneOrMore rule does not match if child rule does not match" {
        checkAll(lowerCaseCodePoints(), upperCaseCodePoints()) { lower, upper ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = oneOrMore(lower.value.toRule())
            }).apply {
                run(toString(upper.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(upper.value)
                }
            }
        }
    }
})

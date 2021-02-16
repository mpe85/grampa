package com.mpe85.grampa.grammar

import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.impl.toRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.positiveInts
import io.kotest.property.checkAll

class RepeatRuleTests : StringSpec({
    "Repeat(n) rule matches if child rule matches exactly n times" {
        checkAll(legalCodePoints(), Arb.positiveInts(10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = repeat(cp.value.toRule(), n)
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
    "Repeat(n) rule does not match if child rule matches less than n times" {
        checkAll(legalCodePoints(), Arb.positiveInts(10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = repeat(cp.value.toRule(), n)
            }).apply {
                val repeated = toString(cp.value).repeat(n - 1)
                run(repeated).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe repeated
                }
            }
        }
    }
    "Repeat(n) rule matches if child rule matches more than n times" {
        checkAll(legalCodePoints(), Arb.positiveInts(10)) { cp, n ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = repeat(cp.value.toRule(), n)
            }).apply {
                val repeated = toString(cp.value).repeat(n + 1)
                run(repeated).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe repeated.removeSuffix(toString(cp.value))
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
})

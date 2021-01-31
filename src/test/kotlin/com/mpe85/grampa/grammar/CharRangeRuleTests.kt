package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll

class CharRangeRuleTests : StringSpec({
    "CharRange rule matches character in range" {
        checkAll<Char, Char> { ch1, ch2 ->
            val lower = if (ch1 < ch2) ch1 else ch2
            val upper = if (ch1 > ch2) ch1 else ch2
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = charRange(lower, upper)
            }).apply {
                checkAll(Arb.char().filter { it in (lower..upper) }) { ch ->
                    run("$ch").apply {
                        matched shouldBe true
                        matchedEntireInput shouldBe true
                        matchedInput shouldBe "$ch"
                        restOfInput shouldBe ""
                    }
                }
            }
        }
    }
    "CharRange rule does not match character out of range" {
        checkAll<Char, Char> { ch1, ch2 ->
            val lower = if (ch1 < ch2) ch1 else ch2
            val upper = if (ch1 > ch2) ch1 else ch2
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = charRange(lower, upper)
            }).apply {
                checkAll(Arb.char().filter { it !in (lower..upper) }) { ch ->
                    run("$ch").apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe "$ch"
                    }
                }
            }
        }
    }
    "CharRange rule does not match empty input" {
        checkAll<Char, Char> { ch1, ch2 ->
            val lower = if (ch1 < ch2) ch1 else ch2
            val upper = if (ch1 > ch2) ch1 else ch2
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = charRange(lower, upper)
            }).apply {
                run("").apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe ""
                }
            }
        }
    }
})

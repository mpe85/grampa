package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class CharRangeRuleTests : StringSpec({
    "CharRange rule matches character in range" {
        checkAll<Char, Char, Char> { ch1, ch2, ch3 ->
            val chars = listOf(ch1, ch2, ch3).sorted()
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = charRange(chars[0], chars[2])
            }).run("${chars[1]}").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "${chars[1]}"
                restOfInput shouldBe ""
            }
        }
    }
    "CharRange rule does not match character out of range" {
        checkAll<Char, Char, Char> { ch1, ch2, ch3 ->
            val chars = listOf(ch1, ch2, ch3).sorted()
            if (chars[1] != chars[0] && chars[1] != chars[2]) {
                Parser(object : AbstractGrammar<Int>() {
                    override fun root() = charRange(chars[0], chars[1])
                }).run("${chars[2]}").apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe "${chars[2]}"
                }
            }
        }
    }
    "CharRange rule does not match empty input" {
        checkAll<Char, Char> { ch1, ch2 ->
            val chars = listOf(ch1, ch2).sorted()
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = charRange(chars[0], chars[1])
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

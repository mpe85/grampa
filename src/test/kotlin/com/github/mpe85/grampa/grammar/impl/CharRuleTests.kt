package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.lowerCaseChars
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.upperCaseChars
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class CharRuleTests : StringSpec({
    "Char rule matches correct character" {
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(ch)
            }).run("$ch").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "$ch"
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase Char rule does not match uppercase character" {
        checkAll(lowerCaseChars()) { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(ch)
            }).apply {
                val uppercase = ch.toUpperCase()
                run("$uppercase").apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe "$uppercase"
                }
            }
        }
    }
    "Uppercase Char rule does not match lowercase character" {
        checkAll(upperCaseChars()) { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(ch)
            }).apply {
                val lowercase = ch.toLowerCase()
                run("$lowercase").apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe "$lowercase"
                }
            }
        }
    }
    "Char rule does not match wrong character" {
        checkAll(Arb.set(Arb.char(), 2..2)) { chars ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(chars.first())
            }).run("${chars.last()}").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe "${chars.last()}"
            }
        }
    }
    "Char rule does not match empty input" {
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(ch)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

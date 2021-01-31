package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
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
        checkAll(Arb.char(CharRange('a', 'z'))) { ch ->
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
        checkAll(Arb.char(CharRange('A', 'Z'))) { ch ->
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
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = char(ch)
            }).apply {
                checkAll(Arb.char().filter { it != ch }) { c ->
                    run("$c").apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe "$c"
                    }
                }
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

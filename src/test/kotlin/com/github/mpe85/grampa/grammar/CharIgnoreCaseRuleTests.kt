package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.checkAll

class CharIgnoreCaseRuleTests : StringSpec({
    "CharIgnoreCase rule matches correct character" {
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(ch)
            }).run("$ch").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe "$ch"
                restOfInput shouldBe ""
            }
        }
    }
    "Lowercase CharIgnoreCase rule matches uppercase character" {
        checkAll(Arb.char(CharRange('a', 'z'))) { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(ch)
            }).apply {
                val uppercase = ch.uppercaseChar()
                run("$uppercase").apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe "$uppercase"
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Uppercase CharIgnoreCase rule matches lowercase character" {
        checkAll(Arb.char(CharRange('A', 'Z'))) { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(ch)
            }).apply {
                val lowercase = ch.lowercaseChar()
                run("$lowercase").apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe "$lowercase"
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "CharIgnoreCase rule does not match wrong character" {
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(ch)
            }).apply {
                checkAll(Arb.char().filter { it.uppercaseChar() != ch.uppercaseChar() }) { c ->
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
    "CharIgnoreCase rule does not match empty input" {
        checkAll<Char> { ch ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = ignoreCase(ch)
            }).run("").apply {
                matched shouldBe false
                matchedEntireInput shouldBe false
                matchedInput shouldBe null
                restOfInput shouldBe ""
            }
        }
    }
})

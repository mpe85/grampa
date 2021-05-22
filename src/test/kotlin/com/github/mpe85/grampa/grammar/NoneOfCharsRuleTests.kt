package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class NoneOfCharsRuleTests : StringSpec({
    fun grammars(chars: Collection<Char>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun start() = noneOfChars(*chars.toCharArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun start() = noneOfChars(chars)
        },
        object : AbstractGrammar<Unit>() {
            override fun start() = noneOfChars(chars.joinToString(""))
        }
    )
    "NoneOfChars rule does not match character in collection" {
        checkAll(Arb.set(Arb.char(), 1..10)) { chars ->
            grammars(chars).forEach { grammar ->
                Parser(grammar).apply {
                    chars.forEach { ch ->
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
    }
    "NoneOfChars rule matches character not in collection" {
        checkAll(Arb.set(Arb.char(), 2..10)) { chars ->
            grammars(chars.drop(1)).forEach { grammar ->
                Parser(grammar).run("${chars.first()}").apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe "${chars.first()}"
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Empty NoneOfChars rule matches any character" {
        grammars(emptySet()).forEach { grammar ->
            Parser(grammar).apply {
                checkAll<Char> { ch ->
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
})

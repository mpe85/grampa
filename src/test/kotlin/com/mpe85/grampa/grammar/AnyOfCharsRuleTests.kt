package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.element
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll

class AnyOfCharsRuleTests : StringSpec({
    fun grammars(chars: List<Char>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfChars(*chars.toCharArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfChars(chars)
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfChars(chars.joinToString(""))
        }
    )
    "AnyOfChars rule matches character in collection/vararg/string" {
        checkAll(Arb.list(Arb.char(), 1..10)) { chars ->
            grammars(chars).forEach { grammar ->
                checkAll(Arb.element(chars)) { ch ->
                    Parser(grammar).apply {
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
    }
})

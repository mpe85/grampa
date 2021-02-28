package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class AnyCharRuleTests : StringSpec({
    "AnyChar rule matches all characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = anyChar()
        }).apply {
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
    "AnyChar rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = anyChar()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

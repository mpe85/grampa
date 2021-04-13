package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SkippableCommandRuleTests : StringSpec({
    "SkippableCommand rule matches when command is executed" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = skippableCommand {
                it.stack.push(Unit)
                it.level shouldBe 0
                it.position shouldNotBe null
            }
        }).run("").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe ""
            restOfInput shouldBe ""
            stackTop shouldBe Unit
        }
    }
    "SkippableCommand rule is skipped when it is part of a Test rule" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = test(
                skippableCommand {
                    it.stack.push(Unit)
                    it.level shouldBe 0
                    it.position shouldNotBe null
                }
            )
        }).run("").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe ""
            restOfInput shouldBe ""
            stackTop shouldBe null
        }
    }
})

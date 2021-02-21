package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SkippableActionRuleTests : StringSpec({
    "SkippableAction rule matches when action succeeds" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = skippableAction {
                it.stack.push(Unit)
                it.level shouldBe 0
                it.position shouldNotBe null
                true
            }
        }).run("").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe ""
            restOfInput shouldBe ""
            stackTop shouldBe Unit
        }
    }
    "SkippableAction rule does not match when action fails" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = action {
                it.stack.push(Unit)
                it.level shouldBe 0
                it.position shouldNotBe null
                false
            }
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
            stackTop shouldBe null
        }
    }
    "SkippableAction rule with succeeding action is skipped when it is part of a Test rule" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = test(
                skippableAction {
                    it.stack.push(Unit)
                    it.level shouldBe 0
                    it.position shouldNotBe null
                    true
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
    "SkippableAction rule with failing action is skipped when it is part of a Test rule" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = test(
                skippableAction {
                    it.stack.push(Unit)
                    it.level shouldBe 0
                    it.position shouldNotBe null
                    false
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

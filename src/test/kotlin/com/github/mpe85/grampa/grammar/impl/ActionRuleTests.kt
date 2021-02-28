package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ActionRuleTests : StringSpec({
    "Action rule matches when action succeeds" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = action {
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
    "Action rule does not match when action fails" {
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
})

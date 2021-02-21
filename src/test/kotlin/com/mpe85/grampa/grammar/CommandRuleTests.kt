package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class CommandRuleTests : StringSpec({
    "Command rule matches when command is executed" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = command {
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
})

package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.impl.plus
import com.github.mpe85.grampa.rule.impl.toRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.checkAll

class CommandRuleTests : StringSpec({
    "Command rule matches when command is executed" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = command {
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
    "Command rule provides matched char sequence of previous rule" {
        checkAll<String> { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = str.toRule<Unit>() + command {
                    it.previousMatch shouldBe str
                }
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe str
                restOfInput shouldBe ""
            }
        }
    }
})

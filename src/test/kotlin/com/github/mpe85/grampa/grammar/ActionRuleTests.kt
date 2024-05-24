package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.Action
import com.github.mpe85.grampa.rule.ActionRule
import com.github.mpe85.grampa.rule.Command
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

class ActionRuleTests : StringSpec({
    "Action rule matches when action succeeds" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = action {
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
            override fun start() = action {
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
    "Action function is correctly converted to action rule" {
        val grammar = object : AbstractGrammar<Unit>() {
            override fun start() = { _: RuleContext<Unit> -> true }.toActionRule()
        }
        grammar.start().shouldBeInstanceOf<ActionRule<Unit>>()
    }
    "Action is correctly converted to action rule" {
        val grammar = object : AbstractGrammar<Unit>() {
            override fun start() = Action<Unit> { true }.toRule()
        }
        grammar.start().shouldBeInstanceOf<ActionRule<Unit>>()
    }
    "Command function is correctly converted to action rule" {
        val grammar = object : AbstractGrammar<Unit>() {
            override fun start() = { _: RuleContext<Unit> -> }.toCommandRule()
        }
        grammar.start().shouldBeInstanceOf<ActionRule<Unit>>()
    }
    "Command is correctly converted to action rule" {
        val grammar = object : AbstractGrammar<Unit>() {
            override fun start() = Command<Unit> {}.toRule()
        }
        grammar.start().shouldBeInstanceOf<ActionRule<Unit>>()
    }
})

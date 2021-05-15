package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.rule.Action
import com.github.mpe85.grampa.rule.Command
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ActionRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val action = Action<String> { true }
        val rule1 = ActionRule(action::run)
        val rule2 = ActionRule(action::run, false)
        val rule3 = ActionRule<String>({ true })
        rule1 shouldBe rule2
        rule1 shouldNotBe rule3
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.hashCode() shouldNotBe rule3.hashCode()
        rule1.toString() shouldBe "ActionRule(action=" +
            "fun com.github.mpe85.grampa.rule.Action<T>.run(" +
            "com.github.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
        rule2.toString() shouldBe "ActionRule(action=" +
            "fun com.github.mpe85.grampa.rule.Action<T>.run(" +
            "com.github.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
        rule3.toString() shouldBe "ActionRule(action=" +
            "(com.github.mpe85.grampa.context.RuleContext<kotlin.String>) -> kotlin.Boolean, skippable=false)"
    }
    "Action function is correctly converted to action rule" {
        val rule = { _: RuleContext<Unit> -> true }.toRule()
        rule.toString() shouldBe "ActionRule(action=" +
            "(com.github.mpe85.grampa.context.RuleContext<kotlin.Unit>) -> kotlin.Boolean, skippable=false)"
    }
    "Action is correctly converted to action rule" {
        val rule = Action<Unit> { true }.toRule()
        rule.toString() shouldBe "ActionRule(action=" +
            "fun com.github.mpe85.grampa.rule.Action<T>.run(" +
            "com.github.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
    }
    "Command is correctly converted to action rule" {
        val rule = Command<Unit> {}.toRule()
        rule.toString() shouldBe "ActionRule(action=" +
            "fun com.github.mpe85.grampa.rule.Action<T>.run(" +
            "com.github.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
    }
})

package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.RuleContext
import com.mpe85.grampa.rule.Action
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ActionRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val action = Action { _: RuleContext<String> -> true }
        val rule1 = ActionRule(action::run)
        val rule2 = ActionRule(action::run, false)
        val rule3 = ActionRule<String>({ true })
        rule1 shouldBe rule2
        rule1 shouldNotBe rule3
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.hashCode() shouldNotBe rule3.hashCode()
        rule1.toString() shouldBe "ActionRule(action=fun com.mpe85.grampa.rule.Action<T>.run(com.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
        rule2.toString() shouldBe "ActionRule(action=fun com.mpe85.grampa.rule.Action<T>.run(com.mpe85.grampa.context.RuleContext<T>): kotlin.Boolean, skippable=false)"
        rule3.toString() shouldBe "ActionRule(action=(com.mpe85.grampa.context.RuleContext<kotlin.String>) -> kotlin.Boolean, skippable=false)"
    }
})

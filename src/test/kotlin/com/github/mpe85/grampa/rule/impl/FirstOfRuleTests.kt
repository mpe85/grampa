package com.github.mpe85.grampa.rule.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class FirstOfRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val rule1 = FirstOfRule<Unit>(emptyList())
        val rule2 = FirstOfRule<Unit>(emptyList())
        rule1 shouldBe rule2
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.toString() shouldBe "FirstOfRule(#children=0)"
        rule2.toString() shouldBe "FirstOfRule(#children=0)"
    }
})

package com.github.mpe85.grampa.rule

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class ChoiceRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val rule1 = ChoiceRule<Unit>(emptyList())
            val rule2 = ChoiceRule<Unit>(emptyList())
            rule1 shouldBe rule2
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.toString() shouldBe "ChoiceRule(#children=0)"
            rule2.toString() shouldBe "ChoiceRule(#children=0)"
        }
    })

package com.mpe85.grampa.rule.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class NeverRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val rule1 = NeverRule<Unit>()
        val rule2 = NeverRule<Unit>()
        rule1 shouldBe rule2
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.toString() shouldBe "NeverRule()"
        rule2.toString() shouldBe "NeverRule()"
    }
})

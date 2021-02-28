package com.github.mpe85.grampa.rule.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class SequenceRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val rule1 = SequenceRule<Unit>(emptyList())
        val rule2 = SequenceRule<Unit>(emptyList())
        rule1 shouldBe rule2
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.toString() shouldBe "SequenceRule(#children=0)"
        rule2.toString() shouldBe "SequenceRule(#children=0)"
    }
})

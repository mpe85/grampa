package com.github.mpe85.grampa.rule

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class EndOfInputRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val rule1 = EndOfInputRule<Unit>()
        val rule2 = EndOfInputRule<Unit>()
        rule1 shouldBe rule2
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.toString() shouldBe "EndOfInputRule()"
        rule2.toString() shouldBe "EndOfInputRule()"
    }
})

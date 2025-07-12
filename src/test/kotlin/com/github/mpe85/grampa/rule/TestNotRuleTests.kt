package com.github.mpe85.grampa.rule

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TestNotRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val rule1 = TestNotRule<String>(EmptyRule())
            val rule2 = TestNotRule<String>(EmptyRule())
            val rule3 = TestNotRule<String>(CharPredicateRule('x'))
            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe "TestNotRule(rule=EmptyRule)"
            rule2.toString() shouldBe "TestNotRule(rule=EmptyRule)"
            rule3.toString() shouldBe "TestNotRule(rule=CharPredicateRule)"
        }
        "Rule is a test rule" { TestNotRule(EmptyRule<Unit>()).testRule shouldBe true }
    })

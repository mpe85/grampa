package com.mpe85.grampa.rule.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TestRuleTests : StringSpec({
  "equals/hashCode/ToString" {
    val rule1 = TestRule<String>(EmptyRule())
    val rule2 = TestRule<String>(EmptyRule())
    val rule3 = TestRule<String>(CharPredicateRule('x'))
    rule1 shouldBe rule2
    rule1 shouldNotBe rule3
    rule1 shouldNotBe Any()
    rule1.hashCode() shouldBe rule2.hashCode()
    rule1.hashCode() shouldNotBe rule3.hashCode()
    rule1.toString() shouldBe "TestRule(rule=EmptyRule())"
    rule2.toString() shouldBe "TestRule(rule=EmptyRule())"
    rule3.toString() shouldBe "TestRule(rule=CharPredicateRule(predicate=(kotlin.Char) -> kotlin.Boolean))"
  }
  "Rule is a test rule" {
    TestRule(EmptyRule<Unit>()).testRule shouldBe true
  }
})

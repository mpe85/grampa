package com.github.mpe85.grampa.rule

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class RepeatRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val empty = EmptyRule<String>()
            val rule1 = RepeatRule(empty, 0, 5)
            val rule2 = RepeatRule(EmptyRule<String>(), 0, 5)
            val rule3 = RepeatRule(empty, 2)
            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe "RepeatRule(rule=EmptyRule(), min=0, max=5)"
            rule2.toString() shouldBe "RepeatRule(rule=EmptyRule(), min=0, max=5)"
            rule3.toString() shouldBe "RepeatRule(rule=EmptyRule(), min=2, max=null)"
        }
        "Create rule fails with invalid min/max values" {
            shouldThrow<IllegalArgumentException> { RepeatRule(EmptyRule<Any>(), -1) }
            shouldThrow<IllegalArgumentException> { RepeatRule(EmptyRule<Any>(), 3, 2) }
        }
    })

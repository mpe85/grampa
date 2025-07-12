package com.github.mpe85.grampa.rule

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class StringRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val rule1 = StringRule<String>("string1")
            val rule2 = StringRule<String>("string1")
            val rule3 = StringRule<String>("string2")
            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe "StringRule(string=string1)"
            rule2.toString() shouldBe "StringRule(string=string1)"
            rule3.toString() shouldBe "StringRule(string=string2)"
        }
    })

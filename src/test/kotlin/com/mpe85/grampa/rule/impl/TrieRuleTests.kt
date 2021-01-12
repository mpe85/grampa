package com.mpe85.grampa.rule.impl

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TrieRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val rule1 = TrieRule<String>("foo", "bar")
        val rule2 = TrieRule<String>(setOf("bar", "foo"))
        val rule3 = TrieRule<String>("foobar")
        rule1 shouldBe rule2
        rule1 shouldNotBe rule3
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.hashCode() shouldNotBe rule3.hashCode()
        rule1.toString() shouldBe "TrieRule(strings=[bar, foo], ignoreCase=false)"
        rule2.toString() shouldBe "TrieRule(strings=[bar, foo], ignoreCase=false)"
        rule3.toString() shouldBe "TrieRule(strings=[foobar], ignoreCase=false)"
    }
})

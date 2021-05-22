package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.toRule
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class OptionalRuleTests : StringSpec({
    "Optional rule matches if child rule matches" {
        checkAll(legalCodePoints()) { cp ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = optional(cp.value.toRule())
            }).run(toString(cp.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe toString(cp.value)
                restOfInput shouldBe ""
            }
        }
    }
    "Optional rule matches if child rule does not match" {
        checkAll(lowerCaseCodePoints(), upperCaseCodePoints()) { lower, upper ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun start() = optional(lower.value.toRule())
            }).run(toString(upper.value)).apply {
                matched shouldBe true
                matchedEntireInput shouldBe false
                matchedInput shouldBe ""
                restOfInput shouldBe toString(upper.value)
            }
        }
    }
})

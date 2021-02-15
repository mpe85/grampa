package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.or
import com.mpe85.grampa.rule.impl.toRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class FirstOfRuleTests : StringSpec({
    fun grammars(rules: List<Rule<Unit>>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun root() = firstOf(*rules.toTypedArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = firstOf(rules)
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = rules.reduce(Rule<Unit>::or)
        }
    )
    "FirstOf rule matches first matching rule" {
        checkAll(Arb.set(Arb.string(1..10, legalCodePoints()), 2..10)) { strings ->
            grammars(strings.map { it.toRule() }).forEach { grammar ->
                val random = strings.random()
                Parser(grammar).run(random).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe (strings.first { random.startsWith(it) } == random)
                    matchedInput shouldBe strings.first { random.startsWith(it) }
                    restOfInput shouldBe random.removePrefix(strings.first { random.startsWith(it) })
                }
            }
        }
    }
    "Empty FirstOf rule matches any input" {
        checkAll(Arb.string(0..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = firstOf()
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe str.isEmpty()
                matchedInput shouldBe ""
                restOfInput shouldBe str
            }
        }
    }
})

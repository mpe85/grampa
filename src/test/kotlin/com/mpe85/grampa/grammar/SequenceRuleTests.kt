package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.and
import com.mpe85.grampa.rule.impl.plus
import com.mpe85.grampa.rule.impl.toRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class SequenceRuleTests : StringSpec({
    fun grammars(rules: List<Rule<Unit>>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun root() = sequence(*rules.toTypedArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = sequence(rules)
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = rules.reduce(Rule<Unit>::and)
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = rules.reduce(Rule<Unit>::plus)
        }
    )
    "Sequence rule matches correct sequence" {
        checkAll(Arb.list(Arb.string(0..10, legalCodePoints()), 2..10)) { strings ->
            grammars(strings.map { it.toRule() }).forEach { grammar ->
                val joined = strings.reduce(String::plus)
                Parser(grammar).run(joined).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe joined
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Empty Sequence rule matches any input" {
        checkAll(Arb.string(0..10, legalCodePoints())) { str ->
            Parser(object : AbstractGrammar<Unit>() {
                override fun root() = sequence()
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe str.isEmpty()
                matchedInput shouldBe ""
                restOfInput shouldBe str
            }
        }
    }
    "Sequence rule matches sequence of stack actions" {
        checkAll(Arb.int(), Arb.int(), Arb.string(1..10, legalCodePoints())) { i1, i2, str ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = sequence(
                    push(i1),
                    push { peek(it) + i2 },
                    sequence(push { pop(1, it) + peek(it) }),
                    optional(action {
                        it.stack.push(0)
                        false
                    })
                )
            }).run(str).apply {
                matched shouldBe true
                matchedEntireInput shouldBe false
                matchedInput shouldBe ""
                restOfInput shouldBe str
                stackTop shouldBe 2 * i1 + i2
                stack.size shouldBe 2
                stack.peek() shouldBe 2 * i1 + i2
                stack.peek(1) shouldBe i1 + i2
            }
        }
    }
})
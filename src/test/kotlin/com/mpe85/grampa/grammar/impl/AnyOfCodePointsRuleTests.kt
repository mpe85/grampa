package com.mpe85.grampa.grammar.impl

import com.ibm.icu.lang.UCharacter.toString
import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll

class AnyOfCodePointsRuleTests : StringSpec({
    fun grammars(codePoints: Collection<Int>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfCodePoints(*codePoints.toIntArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfCodePoints(codePoints)
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = anyOfCodePoints(StringBuilder().run {
                codePoints.forEach { appendCodePoint(it) }
                toString()
            })
        }
    )
    "AnyOfCodePoints rule matches codepoint in collection" {
        checkAll(Arb.set(legalCodePoints(), 1..10)) { codePoints ->
            grammars(codePoints.map { it.value }).forEach { grammar ->
                Parser(grammar).apply {
                    codePoints.forEach { cp ->
                        run(toString(cp.value)).apply {
                            matched shouldBe true
                            matchedEntireInput shouldBe true
                            matchedInput shouldBe toString(cp.value)
                            restOfInput shouldBe ""
                        }
                    }
                }
            }
        }
    }
    "AnyOfCodePoints rule does not match codepoint not in collection" {
        checkAll(Arb.set(legalCodePoints(), 2..10)) { codePoints ->
            grammars(codePoints.map { it.value }.drop(1)).forEach { grammar ->
                Parser(grammar).run(toString(codePoints.first().value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(codePoints.first().value)
                }
            }
        }
    }
    "Empty AnyOfCodePoints rule matches no codepoint" {
        grammars(emptySet()).forEach { grammar ->
            Parser(grammar).apply {
                checkAll(legalCodePoints()) { cp ->
                    run(toString(cp.value)).apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe toString(cp.value)
                    }
                }
            }
        }
    }
})

package com.mpe85.grampa.grammar

import com.mpe85.grampa.legalCodePoints
import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class StringsRuleTests : StringSpec({
    fun grammars(strings: Collection<String>) = listOf(
        object : AbstractGrammar<Unit>() {
            override fun root() = strings(*strings.toTypedArray())
        },
        object : AbstractGrammar<Unit>() {
            override fun root() = strings(strings)
        }
    )
    "Strings rule matches string in collection" {
        checkAll(Arb.set(Arb.string(1..10, legalCodePoints()), 1..10)) { strings ->
            grammars(strings).forEach { grammar ->
                Parser(grammar).apply {
                    strings.forEach { str ->
                        run(str).apply {
                            matched shouldBe true
                            matchedEntireInput shouldBe true
                            matchedInput shouldBe str
                            restOfInput shouldBe ""
                        }
                    }
                }
            }
        }
    }
    "Strings rule does not match string not in collection" {
        checkAll(Arb.set(Arb.string(1..10, legalCodePoints()), 2..10)) { strings ->
            grammars(strings.drop(1)).forEach { grammar ->
                Parser(grammar).run(strings.first()).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe strings.first()
                }
            }
        }
    }
    "Empty Strings rule matches no string" {
        grammars(emptySet()).forEach { grammar ->
            Parser(grammar).apply {
                checkAll<String> { str ->
                    run(str).apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe str
                    }
                }
            }
        }
    }
})

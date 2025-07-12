package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.checkAll

class ConditionalRuleTests :
    StringSpec({
        "Conditional(then,else) rule matches when condition evaluates to true and then rule matches" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ true }, letter(), digit())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe true
                        matchedEntireInput shouldBe true
                        matchedInput shouldBe toString(letter.value)
                        restOfInput shouldBe ""
                    }
            }
        }
        "Conditional(then) rule matches when condition evaluates to true and then rule matches" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ true }, letter())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe true
                        matchedEntireInput shouldBe true
                        matchedInput shouldBe toString(letter.value)
                        restOfInput shouldBe ""
                    }
            }
        }
        "Conditional(then,else) rule matches when condition evaluates to false and else rule matches" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ false }, digit(), letter())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe true
                        matchedEntireInput shouldBe true
                        matchedInput shouldBe toString(letter.value)
                        restOfInput shouldBe ""
                    }
            }
        }
        "Conditional(then,else) rule does not match when condition evaluates to true and then rule does not match" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ true }, digit(), letter())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe toString(letter.value)
                    }
            }
        }
        "Conditional(then) rule does not match when condition evaluates to true and then rule does not match" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ true }, digit())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe toString(letter.value)
                    }
            }
        }
        "Conditional(then,else) rule does not match when condition evaluates to false and else rule does not match" {
            checkAll(Codepoint.az()) { letter ->
                Parser(
                        object : AbstractGrammar<Unit>(), ValidGrammar {
                            override fun start() = conditional({ false }, letter(), digit())
                        }
                    )
                    .run(toString(letter.value))
                    .apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe toString(letter.value)
                    }
            }
        }
    })

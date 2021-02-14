package com.mpe85.grampa.grammar

import com.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class CrLfRuleTests : StringSpec({
    "CRLF rule matches CRLF character" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = crlf()
        }).run("\r\n").apply {
            matched shouldBe true
            matchedEntireInput shouldBe true
            matchedInput shouldBe "\r\n"
            restOfInput shouldBe ""
        }
    }
    "CRLF rule does not match non-CRLF strings" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = crlf()
        }).apply {
            checkAll(Arb.string().filterNot { it.startsWith("\r\n") }) { str ->
                run(str).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe str
                }
            }
        }
    }
    "CRLF rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun root() = crlf()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

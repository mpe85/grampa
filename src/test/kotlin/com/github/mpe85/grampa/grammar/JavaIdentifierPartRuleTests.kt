package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll
import java.lang.Character.isJavaIdentifierPart

class JavaIdentifierPartRuleTests : StringSpec({
    "JavaIdentifierPart rule matches all java identifier part characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = javaIdentifierPart()
        }).apply {
            checkAll(Arb.char().filter { isJavaIdentifierPart(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe ch.toString()
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "JavaIdentifierPart rule matches all java identifier part codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = javaIdentifierPart()
        }).apply {
            checkAll(legalCodePoints().filter { isJavaIdentifierPart(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "JavaIdentifierPart rule does not match non java identifier part characters" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = javaIdentifierPart()
        }).apply {
            checkAll(Arb.char().filterNot { isJavaIdentifierPart(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe ch.toString()
                }
            }
        }
    }
    "JavaIdentifierPart rule does not match non java identifier part codepoints" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = javaIdentifierPart()
        }).apply {
            checkAll(legalCodePoints().filterNot { isJavaIdentifierPart(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "JavaIdentifierPart rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>() {
            override fun start() = javaIdentifierPart()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

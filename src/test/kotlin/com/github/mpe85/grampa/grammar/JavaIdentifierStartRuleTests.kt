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
import java.lang.Character.isJavaIdentifierStart

class JavaIdentifierStartRuleTests :
    StringSpec({
        "JavaIdentifierStart rule matches all java identifier start characters" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = javaIdentifierStart()
                    }
                )
                .apply {
                    checkAll(Arb.char().filter { isJavaIdentifierStart(it) }) { ch ->
                        run(ch.toString()).apply {
                            matched shouldBe true
                            matchedEntireInput shouldBe true
                            matchedInput shouldBe ch.toString()
                            restOfInput shouldBe ""
                        }
                    }
                }
        }
        "JavaIdentifierStart rule matches all java identifier start codepoints" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = javaIdentifierStart()
                    }
                )
                .apply {
                    checkAll(legalCodePoints().filter { isJavaIdentifierStart(it.value) }) { cp ->
                        run(toString(cp.value)).apply {
                            matched shouldBe true
                            matchedEntireInput shouldBe true
                            matchedInput shouldBe toString(cp.value)
                            restOfInput shouldBe ""
                        }
                    }
                }
        }
        "JavaIdentifierStart rule does not match non java identifier start characters" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = javaIdentifierStart()
                    }
                )
                .apply {
                    checkAll(Arb.char().filterNot { isJavaIdentifierStart(it) }) { ch ->
                        run(ch.toString()).apply {
                            matched shouldBe false
                            matchedEntireInput shouldBe false
                            matchedInput shouldBe null
                            restOfInput shouldBe ch.toString()
                        }
                    }
                }
        }
        "JavaIdentifierStart rule does not match non java identifier start codepoints" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = javaIdentifierStart()
                    }
                )
                .apply {
                    checkAll(legalCodePoints().filterNot { isJavaIdentifierStart(it.value) }) { cp
                        ->
                        run(toString(cp.value)).apply {
                            matched shouldBe false
                            matchedEntireInput shouldBe false
                            matchedInput shouldBe null
                            restOfInput shouldBe toString(cp.value)
                        }
                    }
                }
        }
        "JavaIdentifierStart rule does not match empty input" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = javaIdentifierStart()
                    }
                )
                .run("")
                .apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe ""
                }
        }
    })

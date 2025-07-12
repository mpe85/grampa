package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.lowerCaseCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.upperCaseCodePoints
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.lang.UCharacter.toUpperCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.az
import io.kotest.property.arbitrary.cyrillic
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll

class StringsIgnoreCaseRuleTests :
    StringSpec({
        fun grammars(strings: Collection<String>) =
            listOf(
                object : AbstractGrammar<Unit>(), ValidGrammar {
                    override fun start() = stringsIgnoreCase(*strings.toTypedArray())
                },
                object : AbstractGrammar<Unit>(), ValidGrammar {
                    override fun start() = stringsIgnoreCase(strings)
                },
            )
        "StringsIgnoreCase rule matches string in collection" {
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
        "Lowercase StringsIgnoreCase rule matches uppercase string in collection" {
            checkAll(Arb.set(Arb.string(1..10, lowerCaseCodePoints()), 1..10)) { strings ->
                grammars(strings).forEach { grammar ->
                    Parser(grammar).apply {
                        strings.forEach { str ->
                            val upperCase = toUpperCase(str)
                            run(upperCase).apply {
                                matched shouldBe true
                                matchedEntireInput shouldBe true
                                matchedInput shouldBe upperCase
                                restOfInput shouldBe ""
                            }
                        }
                    }
                }
            }
        }
        "Uppercase StringsIgnoreCase rule matches lowercase string in collection" {
            checkAll(Arb.set(Arb.string(1..10, upperCaseCodePoints()), 1..10)) { strings ->
                grammars(strings).forEach { grammar ->
                    Parser(grammar).apply {
                        strings.forEach { str ->
                            val lowerCase = toLowerCase(str)
                            run(lowerCase).apply {
                                matched shouldBe true
                                matchedEntireInput shouldBe true
                                matchedInput shouldBe lowerCase
                                restOfInput shouldBe ""
                            }
                        }
                    }
                }
            }
        }
        "StringsIgnoreCase rule does not match string not in collection" {
            checkAll(
                Arb.set(Arb.string(1..10, Codepoint.az()), 2..10),
                Arb.string(1..10, Codepoint.cyrillic()),
            ) { strings, string ->
                grammars(strings).forEach { grammar ->
                    Parser(grammar).run(string).apply {
                        matched shouldBe false
                        matchedEntireInput shouldBe false
                        matchedInput shouldBe null
                        restOfInput shouldBe string
                    }
                }
            }
        }
        "Empty StringsIgnoreCase rule matches no string" {
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

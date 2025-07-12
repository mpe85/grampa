package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class AsciiRuleTests :
    StringSpec({
        "Ascii rule matches all ASCII codepoints" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = ascii()
                    }
                )
                .apply {
                    checkAll(Codepoint.ascii()) { cp ->
                        run(toString(cp.value)).apply {
                            matched shouldBe true
                            matchedEntireInput shouldBe true
                            matchedInput shouldBe toString(cp.value)
                            restOfInput shouldBe ""
                        }
                    }
                }
        }
        "Ascii rule does not match non-ASCII codepoints" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = ascii()
                    }
                )
                .apply {
                    checkAll(
                        legalCodePoints().filterNot {
                            it.value in Char.MIN_VALUE.code..Byte.MAX_VALUE.toInt()
                        }
                    ) { cp ->
                        run(toString(cp.value)).apply {
                            matched shouldBe false
                            matchedEntireInput shouldBe false
                            matchedInput shouldBe null
                            restOfInput shouldBe toString(cp.value)
                        }
                    }
                }
        }
        "Ascii rule does not match empty input" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = ascii()
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

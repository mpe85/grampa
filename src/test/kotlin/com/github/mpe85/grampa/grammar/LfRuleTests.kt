package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class LfRuleTests :
    StringSpec({
        "LF rule matches LF character" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = lf()
                    }
                )
                .run("\n")
                .apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe "\n"
                    restOfInput shouldBe ""
                }
        }
        "LF rule does not match non-LF codepoints" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = lf()
                    }
                )
                .apply {
                    checkAll(legalCodePoints().filterNot { it.value == '\n'.code }) { cp ->
                        run(toString(cp.value)).apply {
                            matched shouldBe false
                            matchedEntireInput shouldBe false
                            matchedInput shouldBe null
                            restOfInput shouldBe toString(cp.value)
                        }
                    }
                }
        }
        "LF rule does not match empty input" {
            Parser(
                    object : AbstractGrammar<Unit>(), ValidGrammar {
                        override fun start() = lf()
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

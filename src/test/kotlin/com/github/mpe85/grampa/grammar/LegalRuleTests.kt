package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.illegalCodePoints
import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class LegalRuleTests : StringSpec({
    "Legal rule matches legal Unicode code points" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = legal()
        }).apply {
            checkAll(legalCodePoints()) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Legal rule does not match illegal Unicode code points" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = legal()
        }).apply {
            checkAll(illegalCodePoints()) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
})

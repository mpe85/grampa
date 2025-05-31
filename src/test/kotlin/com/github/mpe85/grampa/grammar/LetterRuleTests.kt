package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.legalCodePoints
import com.github.mpe85.grampa.parser.Parser
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.lang.UCharacter.toString
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.checkAll

class LetterRuleTests : StringSpec({
    "Letter rule matches all letter characters" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = letter()
        }).apply {
            checkAll(Arb.char().filter { Character.isLetter(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe ch.toString()
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Letter rule matches all letter codepoints" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = letter()
        }).apply {
            checkAll(legalCodePoints().filter { UCharacter.isLetter(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe toString(cp.value)
                    restOfInput shouldBe ""
                }
            }
        }
    }
    "Letter rule does not match non-letter characters" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = letter()
        }).apply {
            checkAll(Arb.char().filterNot { Character.isLetter(it) }) { ch ->
                run(ch.toString()).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe ch.toString()
                }
            }
        }
    }
    "Letter rule does not match non-letter codepoints" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = letter()
        }).apply {
            checkAll(legalCodePoints().filterNot { UCharacter.isLetter(it.value) }) { cp ->
                run(toString(cp.value)).apply {
                    matched shouldBe false
                    matchedEntireInput shouldBe false
                    matchedInput shouldBe null
                    restOfInput shouldBe toString(cp.value)
                }
            }
        }
    }
    "Letter rule does not match empty input" {
        Parser(object : AbstractGrammar<Unit>(), ValidGrammar {
            override fun start() = letter()
        }).run("").apply {
            matched shouldBe false
            matchedEntireInput shouldBe false
            matchedInput shouldBe null
            restOfInput shouldBe ""
        }
    }
})

package com.github.mpe85.grampa.grammar.impl

import com.github.mpe85.grampa.parser.Parser
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.checkAll

class PushRuleTests : StringSpec({
    "Push rule pushes value on parser stack" {
        checkAll<Int> { int ->
            Parser(object : AbstractGrammar<Int>() {
                override fun root() = push(int)
            }).run("").apply {
                matched shouldBe true
                matchedEntireInput shouldBe true
                matchedInput shouldBe ""
                restOfInput shouldBe ""
                stackTop shouldBe int
            }
        }
    }
})

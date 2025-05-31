package com.github.mpe85.grampa.parser

import com.github.mpe85.grampa.grammar.Grammar
import com.github.mpe85.grampa.grammar.ValidGrammar
import com.github.mpe85.grampa.rule.StringRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParseRunnerTests : StringSpec({
    "Run parser" {
        Parser(object : Grammar<Unit>, ValidGrammar {
            override fun start() = StringRule<Unit>("foo")
        }).apply {
            run("foo").matched shouldBe true
            run("foobar").matched shouldBe true
            run("bar").matched shouldBe false
        }
    }
})

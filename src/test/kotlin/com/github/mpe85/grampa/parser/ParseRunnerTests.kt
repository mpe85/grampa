package com.github.mpe85.grampa.parser

import com.github.mpe85.grampa.grammar.Grammar
import com.github.mpe85.grampa.rule.impl.StringRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class ParseRunnerTests : StringSpec({
    "Run parser" {
        Parser(object : Grammar<Unit> {
            override fun start() = StringRule<Unit>("foo")
        }).apply {
            run("foo").matched shouldBe true
            run("foobar").matched shouldBe true
            run("bar").matched shouldBe false
        }
    }
})

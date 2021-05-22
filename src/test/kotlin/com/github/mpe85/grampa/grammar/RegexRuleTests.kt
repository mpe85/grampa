package com.github.mpe85.grampa.grammar

import com.github.mpe85.grampa.parser.Parser
import com.github.mpe85.grampa.rule.toRegexRule
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll

class RegexRuleTests : StringSpec({
    val patterns = listOf(
        "[A-Z]{5,9}",
        "[0-3]([a-c]|[e-g]{1,2})",
        "([a-z0-9]+)[@]([a-z0-9]+)[.]([a-z0-9]+)",
        "(\\d+)",
        "(\\D+)",
        "(\\w+)",
        "(\\W+)"
    )
    "Regex rule matches correct pattern" {
        patterns.forEach { pattern ->
            checkAll(Arb.stringPattern(pattern)) { str ->
                Parser(object : AbstractGrammar<Unit>() {
                    override fun start() = pattern.toRegexRule<Unit>()
                }).run(str).apply {
                    matched shouldBe true
                    matchedEntireInput shouldBe true
                    matchedInput shouldBe str
                    restOfInput shouldBe ""
                }
            }
        }
    }
})

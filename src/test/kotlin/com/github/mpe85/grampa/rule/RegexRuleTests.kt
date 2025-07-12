package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.regex.Pattern

class RegexRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val pattern = Pattern.compile("[a]{3}")
            val rule1 = RegexRule<String>(pattern)
            val rule2 = RegexRule<String>(pattern)
            val rule3 = RegexRule<String>("[a]{3}")
            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe "RegexRule(pattern=[a]{3})"
            rule2.toString() shouldBe "RegexRule(pattern=[a]{3})"
            rule3.toString() shouldBe "RegexRule(pattern=[a]{3})"
        }
        "Rule match" {
            mockk<ParserContext<String>> {
                    every { restOfInput } returns "aaa"
                    every { advanceIndex(3) } returns true
                }
                .let { RegexRule<String>("[a]{3}").match(it) shouldBe true }
            mockk<ParserContext<String>> {
                    every { restOfInput } returns "aaa"
                    every { advanceIndex(3) } returns false
                }
                .let { RegexRule<String>("[a]{3}").match(it) shouldBe false }
        }
    })

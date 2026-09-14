package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk

class CodePointPredicateRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val predicate: (Int) -> Boolean = @JvmSerializableLambda { it == 'a'.code }
            val rule1 = CodePointPredicateRule<String>(predicate)
            val rule2 = CodePointPredicateRule<String>(predicate)
            val rule3 = CodePointPredicateRule<String> @JvmSerializableLambda { it == 'b'.code }

            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe
                "CodePointPredicateRule(predicate=(kotlin.Int) -> kotlin.Boolean)"
            rule2.toString() shouldBe
                "CodePointPredicateRule(predicate=(kotlin.Int) -> kotlin.Boolean)"
            rule3.toString() shouldBe
                "CodePointPredicateRule(predicate=(kotlin.Int) -> kotlin.Boolean)"
        }
        "Rule match" {
            val ctx =
                mockk<ParserContext<String>> {
                    every { atEndOfInput } returns false
                    every { currentCodePoint } returns 'a'.code
                    every { advanceIndex(1) } returns true
                }
            CodePointPredicateRule<String>('a'.code).match(ctx) shouldBe true
            CodePointPredicateRule<String>('b'.code).match(ctx) shouldBe false
        }
    })

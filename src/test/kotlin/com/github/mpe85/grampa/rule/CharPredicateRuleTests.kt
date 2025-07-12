package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.function.Predicate

class CharPredicateRuleTests :
    StringSpec({
        "equals/hashCode/ToString" {
            val predicate = Predicate { ch: Char -> ch == 'a' }
            val rule1 = CharPredicateRule<String>(predicate::test)
            val rule2 = CharPredicateRule<String>(predicate::test)
            val rule3 = CharPredicateRule<String> @JvmSerializableLambda { it == 'a' }
            rule1 shouldBe rule2
            rule1 shouldNotBe rule3
            rule1 shouldNotBe Any()
            rule1.hashCode() shouldBe rule2.hashCode()
            rule1.hashCode() shouldNotBe rule3.hashCode()
            rule1.toString() shouldBe
                "CharPredicateRule(predicate=" +
                    "fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)"
            rule2.toString() shouldBe
                "CharPredicateRule(predicate=" +
                    "fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)"
            rule3.toString() shouldBe "CharPredicateRule(predicate=(kotlin.Char) -> kotlin.Boolean)"
        }
        "Rule match" {
            val ctx =
                mockk<ParserContext<String>> {
                    every { atEndOfInput } returns false
                    every { currentChar } returns 'a'
                    every { advanceIndex(1) } returns true
                }
            CharPredicateRule<String>('a').match(ctx) shouldBe true
            CharPredicateRule<String>('b').match(ctx) shouldBe false
        }
    })

package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.function.Predicate

class CodePointPredicateRuleTests : StringSpec({
  "equals/hashCode/ToString" {
    val predicate = Predicate { cp: Int -> cp == 'a'.toInt() }
    val rule1 = CodePointPredicateRule<String>(predicate::test)
    val rule2 = CodePointPredicateRule<String>(predicate::test)
    val rule3 = CodePointPredicateRule<String>('a')
    rule1 shouldBe rule2
    rule1 shouldNotBe rule3
    rule1 shouldNotBe Any()
    rule1.hashCode() shouldBe rule2.hashCode()
    rule1.hashCode() shouldNotBe rule3.hashCode()
    rule1.toString() shouldBe "CodePointPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)"
    rule2.toString() shouldBe "CodePointPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)"
    rule3.toString() shouldBe "CodePointPredicateRule(predicate=(kotlin.Int) -> kotlin.Boolean)"
  }
  "Rule match" {
    val ctx = mockk<ParserContext<String>>().apply {
      every { atEndOfInput } returns false
      every { currentCodePoint } returns 'a'.toInt()
      every { advanceIndex(1) } returns true
    }
    CodePointPredicateRule<String>('a').match(ctx) shouldBe true
    CodePointPredicateRule<String>('b').match(ctx) shouldBe false
  }
})

package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.context.RuleContext
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import java.util.function.Predicate

class ConditionalRuleTests : StringSpec({
    "equals/hashCode/ToString" {
        val predicate = Predicate<RuleContext<String>> { true }
        val empty = EmptyRule<String>()
        val never = NeverRule<String>()
        val rule1 = ConditionalRule(predicate::test, empty, never)
        val rule2 = ConditionalRule(predicate::test, EmptyRule(), NeverRule())
        val rule3 = ConditionalRule(predicate::test, empty)
        rule1 shouldBe rule2
        rule1 shouldNotBe rule3
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.hashCode() shouldNotBe rule3.hashCode()
        rule1.toString() shouldBe "ConditionalRule(condition=" +
            "fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule, elseRule=NeverRule)"
        rule2.toString() shouldBe "ConditionalRule(condition=" +
            "fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule, elseRule=NeverRule)"
        rule3.toString() shouldBe "ConditionalRule(condition=" +
            "fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule, elseRule=null)"
    }
    "Rule match" {
        val ctx = mockk<ParserContext<String>>().apply {
            every { atEndOfInput } returns false
            every { currentChar } returns 'a'
            every { advanceIndex(1) } returns true
        }
        ConditionalRule<String>({ true }, CharPredicateRule('a')).match(ctx) shouldBe true
        ConditionalRule<String>({ false }, CharPredicateRule('b')).match(ctx) shouldBe true
        ConditionalRule<String>({ true }, CharPredicateRule('a'), CharPredicateRule('b')).match(ctx) shouldBe true
        ConditionalRule<String>({ false }, CharPredicateRule('a'), CharPredicateRule('b')).match(ctx) shouldBe false
    }
})

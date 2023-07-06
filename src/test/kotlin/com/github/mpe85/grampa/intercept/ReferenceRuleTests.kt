package com.github.mpe85.grampa.intercept

import com.github.mpe85.grampa.rule.EmptyRule
import com.github.mpe85.grampa.rule.ReferenceRule
import com.github.mpe85.grampa.rule.Rule
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.mockk
import java.util.concurrent.Callable

class ReferenceRuleTests : StringSpec({
    class RuleMethods : Callable<Rule<Int>> {
        fun rule1() = EmptyRule<Int>()
        fun rule2() = EmptyRule<Int>()
        override fun call() = EmptyRule<Int>()
    }

    fun createReferenceRule(ruleMethod: String): ReferenceRule<Int> {
        val intercept = RuleMethodInterceptor<Int>().let {
            {
                it.intercept(RuleMethods::class.java.getDeclaredMethod(ruleMethod), RuleMethods())
            }
        }
        shouldNotThrowAny { intercept() }
        // The second intercept will cause the creation of a ReferenceRule
        val rule = shouldNotThrowAny { intercept() }
        rule.shouldBeInstanceOf<ReferenceRule<Int>>()
        return rule
    }
    "equals/hashCode/ToString" {
        val rule1 = createReferenceRule("rule1")
        val rule2 = createReferenceRule("rule1")
        val rule3 = createReferenceRule("rule2")
        rule1 shouldBe rule2
        rule1 shouldNotBe rule3
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.hashCode() shouldNotBe rule3.hashCode()
        rule1.toString() shouldBe "ReferenceRuleImpl(referencedRuleHash=${rule1.referencedRuleHash})"
        rule2.toString() shouldBe "ReferenceRuleImpl(referencedRuleHash=${rule2.referencedRuleHash})"
        rule3.toString() shouldBe "ReferenceRuleImpl(referencedRuleHash=${rule3.referencedRuleHash})"
    }
    "ReferenceRule does not match" {
        createReferenceRule("rule1").match(mockk()) shouldBe false
    }
})

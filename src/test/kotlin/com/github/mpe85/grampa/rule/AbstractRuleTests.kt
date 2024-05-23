package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.stringify
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.mockk

class AbstractRuleTests : StringSpec({
    class OnlyChildRule : AbstractRule<String> {
        constructor()
        constructor(child: Rule<String>) : super(child)

        override fun match(context: ParserContext<String>) = false
        override fun toString() = stringify()
    }
    "equals/hashCode/ToString" {
        val rule1 = OnlyChildRule()
        val rule2 = OnlyChildRule()
        rule1 shouldBe rule2
        rule1 shouldNotBe Any()
        rule1.hashCode() shouldBe rule2.hashCode()
        rule1.toString() shouldBe "\$OnlyChildRule()"
        rule2.toString() shouldBe "\$OnlyChildRule()"
    }
    "Replace reference rule" {
        OnlyChildRule(mockk<ReferenceRule<String>>()).apply {
            replaceReferenceRule(0, NeverRule()) shouldNotBe null
        }
        OnlyChildRule(EmptyRule()).apply {
            shouldThrow<IllegalArgumentException> {
                replaceReferenceRule(0, NeverRule())
            }
        }
    }
    "Get child rule" {
        OnlyChildRule(EmptyRule()).child shouldNotBe null
        OnlyChildRule().child shouldBe null
    }
})

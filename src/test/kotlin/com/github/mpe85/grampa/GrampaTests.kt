package com.github.mpe85.grampa

import com.github.mpe85.grampa.grammar.impl.AbstractGrammar
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.rule.impl.CharPredicateRule
import com.github.mpe85.grampa.rule.impl.EmptyRule
import com.github.mpe85.grampa.rule.impl.FirstOfRule
import com.github.mpe85.grampa.rule.impl.RepeatRule
import com.github.mpe85.grampa.rule.impl.SequenceRule
import com.github.mpe85.grampa.rule.impl.times
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

open class TestGrammar(val dummy: String?) : AbstractGrammar<String>() {
    constructor() : this(null)

    override fun root() = expr('a')
    protected open fun expr(c: Char): Rule<String> = firstOf(
        char(c),
        sequence(
            empty(),
            root(66),
            noop(),
            char('('),
            root(),
            char(')')
        )
    )

    protected open fun root(i: Int) = i * empty()
    protected open fun noop() = empty()
    open fun verifyRules() = root().apply {
        shouldBeInstanceOf<FirstOfRule<String>>()
        children.size shouldBe 2
        children[0].shouldBeInstanceOf<CharPredicateRule<String>>()
        children[1].shouldBeInstanceOf<SequenceRule<String>>()
        children[1].children.size shouldBe 6
        children[1].children[0].shouldBeInstanceOf<EmptyRule<String>>()
        children[1].children[1].shouldBeInstanceOf<RepeatRule<String>>()
        children[1].children[2].shouldBeInstanceOf<EmptyRule<String>>()
        children[1].children[3].shouldBeInstanceOf<CharPredicateRule<String>>()
        children[1].children[4].shouldBeInstanceOf<FirstOfRule<String>>()
        children[1].children[5].shouldBeInstanceOf<CharPredicateRule<String>>()
        children[1].children[4] shouldBe this
    }
}

class GrampaTests : StringSpec({
    "Create grammar" {
        TestGrammar::class.createGrammar().apply {
            dummy shouldBe null
            verifyRules()
        }
        TestGrammar::class.createGrammar("foo").apply {
            dummy shouldBe "foo"
            verifyRules()
        }
        shouldThrow<IllegalArgumentException> { TestGrammar::class.createGrammar(4711) }
        shouldThrow<IllegalArgumentException> { TestGrammar::class.createGrammar("foo", "bar") }
    }
    "Non-overridable rule method" {
        open class InvalidGrammar : AbstractGrammar<String>() {
            override fun root() = expr('a')

            @SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
            private fun expr(c: Char) = char(c)
        }
        shouldThrow<IllegalArgumentException> { InvalidGrammar::class.createGrammar() }
    }
    "No no-arg constructor grammar" {
        open class NoDefaultCtorTestGrammar(dummy: String) : TestGrammar(dummy)
        shouldThrow<IllegalArgumentException> { NoDefaultCtorTestGrammar::class.createGrammar() }
    }
    "Final rule method grammar" {
        class FinalRuleMethodTestGrammar : TestGrammar() {
            override fun noop() = empty()
        }
        shouldThrow<IllegalArgumentException> { FinalRuleMethodTestGrammar::class.createGrammar() }
    }
    "Grammar inheritance" {
        open class SuperGrammar : TestGrammar() {
            @SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
            override fun root(): Rule<String> = sequence(char('a'), root())
            override fun verifyRules() = root().apply {
                shouldBeInstanceOf<SequenceRule<String>>()
                children.size shouldBe 2
                children[0].shouldBeInstanceOf<CharPredicateRule<String>>()
                children[1].shouldBeInstanceOf<SequenceRule<String>>()
                children[1] shouldBe this
            }
        }

        open class SubGrammar : SuperGrammar() {
            override fun root() = super.root().also { it.toString() }
        }
        SuperGrammar::class.createGrammar().verifyRules()
        SubGrammar::class.createGrammar().verifyRules()
    }
})

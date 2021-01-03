package com.mpe85.grampa

import com.mpe85.grampa.grammar.AbstractGrammar
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.CharPredicateRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.SequenceRule
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

open class TestGrammar(val dummy: String?) : AbstractGrammar<String>() {
  constructor() : this(null)

  override fun root() = expr('a')
  protected open fun expr(c: Char): Rule<String> = firstOf(
    character(c),
    sequence(
      empty(),
      root(66),
      noop(),
      character('('),
      root(),
      character(')')
    )
  )

  protected open fun root(i: Int) = empty()
  protected open fun noop() = empty()
  open fun verifyRules() = root().apply {
    /*shouldBeInstanceOf<FirstOfRule<String>>()
    children.size shouldBe 2
    children[0].shouldBeInstanceOf<CharPredicateRule<String>>()
    children[1].shouldBeInstanceOf<SequenceRule<String>>()
    children[1].children.size shouldBe 6
    children[1].children[0].shouldBeInstanceOf<EmptyRule<String>>()
    children[1].children[1].shouldBeInstanceOf<EmptyRule<String>>()
    children[1].children[2].shouldBeInstanceOf<EmptyRule<String>>()
    children[1].children[3].shouldBeInstanceOf<CharPredicateRule<String>>()
    children[1].children[4].shouldBeInstanceOf<FirstOfRule<String>>()
    children[1].children[5].shouldBeInstanceOf<CharPredicateRule<String>>()*/
    //children[1].children[4] shouldBe this
  }
}

class InvalidGrammar : AbstractGrammar<String>() {
  override fun root() = expr('a')

  @SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
  private fun expr(c: Char): Rule<String> = firstOf(
    character(c),
    sequence(
      character('('),
      expr(c),
      character(')')
    )
  )
}

class NoDefaultCtorTestGrammar(dummy: String) : TestGrammar(dummy)

class FinalRuleMethodTestGrammar : TestGrammar() {
  override fun noop() = empty()
}

class StaticRuleMethodTestGrammar : TestGrammar() {
  companion object {
    @JvmStatic
    fun staticRule() = EmptyRule<String>()
  }
}

open class SuperGrammar : TestGrammar() {
  @SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
  override fun root(): Rule<String> = sequence(character('a'), this.root())
  override fun verifyRules() = root().apply {
    (this is SequenceRule<String>) shouldBe true
    children.size shouldBe 2
    (children[0] is CharPredicateRule<String>) shouldBe true
    (children[1] is SequenceRule<String>) shouldBe true
    children[1] shouldBe this
  }
}

open class SubGrammar : SuperGrammar() {
  override fun root() = super.root().also { it.toString() }
}

class VarArgsTestGrammar : TestGrammar() {
  fun var1(vararg args: List<Any>) = empty()
  fun var2(vararg args: List<Any>) = empty()
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
  "Invalid grammar" {
    shouldThrow<IllegalArgumentException> { InvalidGrammar::class.createGrammar() }
  }
  "No default constructor grammar" {
    shouldThrow<IllegalArgumentException> { NoDefaultCtorTestGrammar::class.createGrammar() }
  }
  "Final rule method grammar" {
    shouldThrow<IllegalArgumentException> { FinalRuleMethodTestGrammar::class.createGrammar() }
  }
  "Static rule method grammar" {
    shouldThrow<IllegalArgumentException> { StaticRuleMethodTestGrammar::class.createGrammar() }
  }
  "Grammar inheritance" {
    SuperGrammar::class.createGrammar().verifyRules()
    SubGrammar::class.createGrammar().verifyRules()
  }
  "Invalid vararg grammar" {
    shouldThrow<IllegalArgumentException> { VarArgsTestGrammar::class.createGrammar() }
  }
})

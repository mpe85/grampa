package com.mpe85.grampa

import com.mpe85.grampa.grammar.AbstractGrammar
import com.mpe85.grampa.rule.Rule

open class TestGrammar : AbstractGrammar<String> {
  var dummy: String? = null
    private set

  constructor()
  constructor(dummy: String) {
    this.dummy = dummy
  }

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

}

package com.mpe85.grampa

import com.mpe85.grampa.grammar.AbstractGrammar
import com.mpe85.grampa.rule.Rule
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

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

package com.mpe85.grampa

import com.mpe85.grampa.rule.Rule
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings

open class SuperParser : TestParser() {

  @SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
  override fun root(): Rule<String> = sequence(character('a'), this.root())
  
}

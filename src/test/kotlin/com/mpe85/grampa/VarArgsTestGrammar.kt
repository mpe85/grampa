package com.mpe85.grampa

class VarArgsTestGrammar : TestGrammar() {
  @SafeVarargs
  fun var1(vararg args: List<Any>) = empty()

  fun var2(vararg args: List<Any>) = empty()

}

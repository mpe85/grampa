package com.mpe85.grampa

import com.mpe85.grampa.rule.impl.EmptyRule

object StaticRuleMethodTestGrammar : TestGrammar() {

  internal fun staticRule() = EmptyRule<String>()

}

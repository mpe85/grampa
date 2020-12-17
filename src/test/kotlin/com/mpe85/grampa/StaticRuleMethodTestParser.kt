package com.mpe85.grampa

import com.mpe85.grampa.rule.impl.EmptyRule

object StaticRuleMethodTestParser : TestParser() {

  internal fun staticRule() = EmptyRule<String>()

}

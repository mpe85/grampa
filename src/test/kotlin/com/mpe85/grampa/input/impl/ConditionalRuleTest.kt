package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.ConditionalRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.NeverRule
import java.util.function.Predicate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ConditionalRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val pred = Predicate { _: RuleContext<String> -> true }
    val empty = EmptyRule<String>()
    val never = NeverRule<String>()
    val rule1 = ConditionalRule(pred::test, empty, never)
    val rule2 = ConditionalRule(pred::test, EmptyRule(), NeverRule())
    val rule3 = ConditionalRule(pred::test, empty)
    Assertions.assertEquals(rule2, rule1)
    Assertions.assertNotEquals(rule3, rule1)
    Assertions.assertNotEquals(Any(), rule1)
    Assertions.assertEquals(rule1.hashCode(), rule2.hashCode())
    Assertions.assertNotEquals(rule1.hashCode(), rule3.hashCode())
    Assertions.assertEquals(
      "ConditionalRule(condition=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule(), elseRule=NeverRule())",
      rule1.toString()
    )
    Assertions.assertEquals(
      "ConditionalRule(condition=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule(), elseRule=NeverRule())",
      rule2.toString()
    )
    Assertions.assertEquals(
      "ConditionalRule(condition=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean, thenRule=EmptyRule(), elseRule=null)",
      rule3.toString()
    )
  }
}
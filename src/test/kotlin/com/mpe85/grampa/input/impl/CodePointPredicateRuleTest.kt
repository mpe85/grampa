package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.CodePointPredicateRule
import java.util.function.Predicate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class CodePointPredicateRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val pred = Predicate { cp: Int -> cp == 'a'.toInt() }
    val rule1 = CodePointPredicateRule<String>(pred::test)
    val rule2 = CodePointPredicateRule<String>(pred::test)
    val rule3: CodePointPredicateRule<String> = CodePointPredicateRule('a')
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals(
      "CodePointPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)",
      rule1.toString()
    )
    assertEquals(
      "CodePointPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)",
      rule2.toString()
    )
    assertEquals(
      "CodePointPredicateRule(predicate=(kotlin.Int) -> kotlin.Boolean)",
      rule3.toString()
    )
  }

  @Test
  fun match(@Mock ctx: RuleContext<String>) {
    Mockito.`when`(ctx.isAtEndOfInput).thenReturn(false)
    Mockito.`when`(ctx.currentCodePoint).thenReturn('a'.toInt())
    Mockito.`when`(ctx.advanceIndex(1)).thenReturn(true)
    val rule1: CodePointPredicateRule<String> = CodePointPredicateRule('a')
    val rule2: CodePointPredicateRule<String> = CodePointPredicateRule('b')
    assertTrue(rule1.match(ctx))
    assertFalse(rule2.match(ctx))
  }
}
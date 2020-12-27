package com.mpe85.grampa.input.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.impl.CharPredicateRule
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
class CharPredicateRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val pred = Predicate { cp: Char -> cp == 'a' }
    val rule1 = CharPredicateRule<String>(pred::test)
    val rule2 = CharPredicateRule<String>(pred::test)
    val rule3 = CharPredicateRule<String>('a')
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals(
      "CharPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)",
      rule1.toString()
    )
    assertEquals(
      "CharPredicateRule(predicate=fun java.util.function.Predicate<T>.test(T): kotlin.Boolean)",
      rule2.toString()
    )
    assertEquals(
      "CharPredicateRule(predicate=(kotlin.Char) -> kotlin.Boolean)",
      rule3.toString()
    )
  }

  @Test
  fun match(@Mock ctx: ParserContext<String?>) {
    Mockito.`when`(ctx.atEndOfInput).thenReturn(false)
    Mockito.`when`(ctx.currentChar).thenReturn('a')
    Mockito.`when`(ctx.advanceIndex(1)).thenReturn(true)
    val rule1 = CharPredicateRule<String?>('a')
    val rule2 = CharPredicateRule<String?>('b')
    assertTrue(rule1.match(ctx))
    assertFalse(rule2.match(ctx))
  }
}
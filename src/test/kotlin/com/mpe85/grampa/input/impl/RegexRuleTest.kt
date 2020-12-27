package com.mpe85.grampa.input.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.impl.RegexRule
import java.util.regex.Pattern
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
class RegexRuleTest {
  @Test
  fun equalsHashCodeToString() {
    val pattern = Pattern.compile("[a]{3}")
    val rule1 = RegexRule<String>(pattern)
    val rule2 = RegexRule<String>(pattern)
    val rule3 = RegexRule<String>("[a]{3}")
    assertTrue(rule1.equals(rule2))
    assertFalse(rule1.equals(rule3))
    assertFalse(rule1.equals(Any()))
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals("RegexRule(pattern=[a]{3})", rule1.toString())
    assertEquals("RegexRule(pattern=[a]{3})", rule2.toString())
    assertEquals("RegexRule(pattern=[a]{3})", rule3.toString())
  }

  @Test
  fun match_valid(@Mock ctx: ParserContext<String?>) {
    Mockito.`when`(ctx.restOfInput).thenReturn("aaa")
    Mockito.`when`(ctx.advanceIndex(3)).thenReturn(true)
    val rule = RegexRule<String?>("[a]{3}")
    assertTrue(rule.match(ctx))
  }

  @Test
  fun match_invalid(@Mock ctx: ParserContext<String?>) {
    Mockito.`when`(ctx.restOfInput).thenReturn("aaa")
    Mockito.`when`(ctx.advanceIndex(3)).thenReturn(false)
    val rule = RegexRule<String?>("[a]{3}")
    assertFalse(rule.match(ctx))
  }
}
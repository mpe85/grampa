package com.mpe85.grampa.input.impl

import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.AbstractRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.NeverRule
import com.mpe85.grampa.util.stringify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class AbstractRuleTest {
  private class SomeRule : AbstractRule<String> {
    constructor()
    constructor(child: Rule<String>) : super(child)

    override fun match(context: RuleContext<String>): Boolean {
      return false
    }

    override fun toString() = stringify()
  }

  @Test
  fun equalsHashCodeToString() {
    val rule1 = SomeRule()
    val rule2 = SomeRule()
    assertEquals(rule1, rule2)
    assertNotEquals(rule1, Any())
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertEquals("SomeRule()", rule1.toString())
    assertEquals("SomeRule()", rule2.toString())
  }

  @Test
  fun replaceReferenceRule_valid(@Mock referenceRule: ReferenceRule<String>) {
    val rule = SomeRule(referenceRule)
    assertNotNull(rule.replaceReferenceRule(0, NeverRule()))
  }

  @Test
  fun replaceReferenceRule_invalid() {
    val rule = SomeRule(EmptyRule())
    assertThrows(IllegalArgumentException::class.java) { rule.replaceReferenceRule(0, NeverRule()) }
  }

  @Test
  fun getChild() {
    val rule1 = SomeRule(EmptyRule())
    val rule2 = SomeRule()
    assertNotNull(rule1.child)
    assertNull(rule2.child)
  }
}

package com.mpe85.grampa.intercept

import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.EmptyRule
import java.util.concurrent.Callable
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.function.ThrowingSupplier
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@TestInstance(PER_CLASS)
@ExtendWith(MockitoExtension::class)
class ReferenceRuleTest {

  internal class RuleMethods : Callable<Rule<Int>> {
    fun rule1(): Rule<Int> {
      return EmptyRule()
    }

    fun rule2(): Rule<Int> {
      return EmptyRule()
    }

    @Throws(Exception::class)
    override fun call(): Rule<Int> {
      return EmptyRule()
    }
  }

  @Test
  fun equalsHashCodeToString() {
    val rule1 = createReferenceRule("rule1")
    val rule2 = createReferenceRule("rule1")
    val rule3 = createReferenceRule("rule2")
    assertEquals(rule1, rule2)
    assertNotEquals(rule1, rule3)
    assertNotEquals(rule1, Any())
    assertEquals(rule1.hashCode(), rule2.hashCode())
    assertNotEquals(rule1.hashCode(), rule3.hashCode())
    assertEquals(
      String.format("ReferenceRule{#children=0, hashCode=%d}", rule1.hashCode()),
      rule1.toString()
    )
    assertEquals(
      String.format("ReferenceRule{#children=0, hashCode=%d}", rule2.hashCode()),
      rule2.toString()
    )
    assertEquals(
      String.format("ReferenceRule{#children=0, hashCode=%d}", rule3.hashCode()),
      rule3.toString()
    )
  }

  @Test
  fun match(@Mock ctx: RuleContext<Int>?) {
    val rule = createReferenceRule("rule1")
    assertFalse(rule.match(ctx))
  }

  private fun createReferenceRule(ruleMethod: String): ReferenceRule<Int> {
    val interceptor = RuleMethodInterceptor<Int>()
    val supplier = ThrowingSupplier {
      interceptor.intercept(
        RuleMethods::class.java.getDeclaredMethod(ruleMethod), RuleMethods()
      )
    }
    assertDoesNotThrow(supplier)
    val rule = assertDoesNotThrow(supplier)
    assertTrue(rule is ReferenceRule<*>)
    return rule as ReferenceRule<Int>
  }

}

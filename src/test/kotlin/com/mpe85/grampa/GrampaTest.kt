package com.mpe85.grampa

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.CharPredicateRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.FirstOfRule
import com.mpe85.grampa.rule.impl.SequenceRule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GrampaTest {

  @Test
  fun createParser_valid_noArgs() {
    val p = TestGrammar::class.createGrammar()
    assertNull(p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_privateRuleMethod() {
    assertThrows(IllegalArgumentException::class.java) { InvalidGrammar::class.createGrammar() }
  }

  @Test
  fun createParser_valid_withArgs() {
    val p = TestGrammar::class.createGrammar("foo")
    assertEquals("foo", p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_ctor() {
    assertThrows(IllegalArgumentException::class.java) {
      TestGrammar::class.createGrammar(4711)
    }
  }

  @Test
  fun createParser_invalid_args() {
    assertThrows(IllegalArgumentException::class.java) {
      TestGrammar::class.createGrammar(4711)
    }
  }

  @Test
  fun createParser_invalid_noDefaultCtor() {
    assertThrows(IllegalArgumentException::class.java) { NoDefaultCtorTestGrammar::class.createGrammar() }
  }

  @Test
  fun createParser_invalid_finalRuleMethod() {
    assertThrows(IllegalArgumentException::class.java) { FinalRuleMethodTestGrammar::class.createGrammar() }
  }

  @Test
  fun createParser_invalid_staticRuleMethod() {
    assertThrows(IllegalArgumentException::class.java) { StaticRuleMethodTestGrammar::class.createGrammar() }
  }

  @Test
  fun createParser_valid_inheritance() {
    val superParser = SuperGrammar::class.createGrammar()
    verifySuperParserRules(superParser.root())
    val subParser = SubGrammar::class.createGrammar()
    verifySuperParserRules(subParser.root())
  }

  private fun verifyTestParserRules(root: Rule<String>) {
    assertTrue(root is FirstOfRule<*>)
    assertEquals(2, root.children.size)
    assertTrue(root.children[0] is CharPredicateRule<*>)
    assertTrue(root.children[1] is SequenceRule<*>)
    val sequenceRule = root.children[1] as SequenceRule<String>
    assertEquals(6, sequenceRule.children.size)
    assertTrue(sequenceRule.children[0] is EmptyRule<*>)
    assertTrue(sequenceRule.children[1] is EmptyRule<*>)
    assertTrue(sequenceRule.children[2] is EmptyRule<*>)
    assertTrue(sequenceRule.children[3] is CharPredicateRule<*>)
    assertTrue(sequenceRule.children[4] is FirstOfRule<*>)
    assertTrue(sequenceRule.children[5] is CharPredicateRule<*>)
    assertEquals(root, sequenceRule.children[4])
  }

  @Test
  fun createParser_invalid_vararg() {
    assertThrows(IllegalArgumentException::class.java) { VarArgsTestGrammar::class.createGrammar() }
  }

  private fun verifySuperParserRules(root: Rule<String>) {
    assertTrue(root is SequenceRule<*>)
    assertEquals(2, root.children.size)
    assertTrue(root.children[0] is CharPredicateRule<*>)
    assertTrue(root.children[1] is SequenceRule<*>)
    assertEquals(root, root.children[1])
  }
}

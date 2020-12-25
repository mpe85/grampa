package com.mpe85.grampa

import com.mpe85.grampa.exception.ParserCreateException
import com.mpe85.grampa.input.impl.FinalRuleMethodTestParser
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
    val p = TestParser::class.createParser()
    assertNull(p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_privateRuleMethod() {
    assertThrows(ParserCreateException::class.java) { InvalidParser::class.createParser() }
  }

  @Test
  fun createParser_valid_withArgs() {
    val p = TestParser::class.createParser("foo")
    assertEquals("foo", p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_ctor() {
    assertThrows(ParserCreateException::class.java) {
      TestParser::class.createParser(4711)
    }
  }

  @Test
  fun createParser_invalid_args() {
    assertThrows(ParserCreateException::class.java) {
      TestParser::class.createParser(4711)
    }
  }

  @Test
  fun createParser_invalid_noDefaultCtor() {
    assertThrows(ParserCreateException::class.java) { NoDefaultCtorTestParser::class.createParser() }
  }

  @Test
  fun createParser_invalid_finalRuleMethod() {
    assertThrows(ParserCreateException::class.java) { FinalRuleMethodTestParser::class.createParser() }
  }

  @Test
  fun createParser_invalid_staticRuleMethod() {
    assertThrows(ParserCreateException::class.java) { StaticRuleMethodTestParser::class.createParser() }
  }

  @Test
  fun createParser_valid_inheritance() {
    val superParser = SuperParser::class.createParser()
    verifySuperParserRules(superParser.root())
    val subParser = SubParser::class.createParser()
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
    assertThrows(ParserCreateException::class.java) { VarArgsTestParser::class.createParser() }
  }

  private fun verifySuperParserRules(root: Rule<String>) {
    assertTrue(root is SequenceRule<*>)
    assertEquals(2, root.children.size)
    assertTrue(root.children[0] is CharPredicateRule<*>)
    assertTrue(root.children[1] is SequenceRule<*>)
    assertEquals(root, root.children[1])
  }
}

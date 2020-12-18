package com.mpe85.grampa

import com.mpe85.grampa.Grampa.createParser
import com.mpe85.grampa.exception.ParserCreateException
import com.mpe85.grampa.input.impl.FinalRuleMethodTestParser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.CharPredicateRule
import com.mpe85.grampa.rule.impl.EmptyRule
import com.mpe85.grampa.rule.impl.FirstOfRule
import com.mpe85.grampa.rule.impl.SequenceRule
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GrampaTest {
  @Test
  fun createParser_valid_noArgs() {
    val p = createParser(TestParser::class.java)
    Assertions.assertNull(p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_privateRuleMethod() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) { createParser(InvalidParser::class.java) }
  }

  @Test
  fun createParser_valid_withArgs() {
    val p = createParser(TestParser::class.java, String::class.java).withArgs("foo")
    Assertions.assertEquals("foo", p.dummy)
    val root = p.root()
    verifyTestParserRules(root)
  }

  @Test
  fun createParser_invalid_ctor() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) { createParser(TestParser::class.java, Int::class.java).withArgs(4711) }
  }

  @Test
  fun createParser_invalid_args() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) { createParser(TestParser::class.java, String::class.java).withArgs(4711) }
  }

  @Test
  fun createParser_invalid_noDefaultCtor() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) {
      createParser(
        NoDefaultCtorTestParser::class.java
      )
    }
  }

  @Test
  fun createParser_invalid_finalRuleMethod() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) {
      createParser(
        FinalRuleMethodTestParser::class.java
      )
    }
  }

  @Test
  fun createParser_invalid_staticRuleMethod() {
    Assertions.assertThrows(
      ParserCreateException::class.java
    ) {
      createParser(
        StaticRuleMethodTestParser::class.java
      )
    }
  }

  @Test
  fun createParser_valid_inheritance() {
    val superParser = createParser(SuperParser::class.java)
    verifySuperParserRules(superParser.root())
    val subParser = createParser(SubParser::class.java)
    verifySuperParserRules(subParser.root())
  }

  private fun verifyTestParserRules(root: Rule<String>) {
    Assertions.assertTrue(root is FirstOfRule<*>)
    Assertions.assertEquals(2, root.children.size)
    Assertions.assertTrue(root.children[0] is CharPredicateRule<*>)
    Assertions.assertTrue(root.children[1] is SequenceRule<*>)
    val sequenceRule = root.children[1] as SequenceRule<String>
    Assertions.assertEquals(6, sequenceRule.children.size)
    Assertions.assertTrue(sequenceRule.children[0] is EmptyRule<*>)
    Assertions.assertTrue(sequenceRule.children[1] is EmptyRule<*>)
    Assertions.assertTrue(sequenceRule.children[2] is EmptyRule<*>)
    Assertions.assertTrue(sequenceRule.children[3] is CharPredicateRule<*>)
    Assertions.assertTrue(sequenceRule.children[4] is FirstOfRule<*>)
    Assertions.assertTrue(sequenceRule.children[5] is CharPredicateRule<*>)
    Assertions.assertEquals(root, sequenceRule.children[4])
  }

  @Test
  fun createParser_invalid_vararg() {
    Assertions.assertThrows(ParserCreateException::class.java) {
      createParser(
        VarArgsTestParser::class.java
      )
    }
  }

  private fun verifySuperParserRules(root: Rule<String>) {
    Assertions.assertTrue(root is SequenceRule<*>)
    Assertions.assertEquals(2, root.children.size)
    Assertions.assertTrue(root.children[0] is CharPredicateRule<*>)
    Assertions.assertTrue(root.children[1] is SequenceRule<*>)
    Assertions.assertEquals(root, root.children[1])
  }
}
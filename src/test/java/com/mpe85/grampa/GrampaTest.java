package com.mpe85.grampa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.exception.ParserCreateException;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.CharPredicateRule;
import com.mpe85.grampa.rule.impl.EmptyRule;
import com.mpe85.grampa.rule.impl.FirstOfRule;
import com.mpe85.grampa.rule.impl.SequenceRule;

public class GrampaTest {
	
	@Test
	public void createParser_valid_noArgs() {
		final TestParser p = Grampa.createParser(TestParser.class);
		assertNull(p.getDummy());
		
		final Rule<String> root = p.root();
		verifyTestParserRules(root);
	}
	
	@Test
	public void createParser_invalid_privateRuleMethod() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(InvalidParser.class));
	}
	
	@Test
	public void createParser_valid_withArgs() {
		final TestParser p = Grampa.createParser(TestParser.class, String.class).withArgs("foo");
		assertEquals("foo", p.getDummy());
		
		final Rule<String> root = p.root();
		verifyTestParserRules(root);
	}
	
	@Test
	public void createParser_invalid_ctor() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(TestParser.class, Integer.class).withArgs(4711));
	}
	
	@Test
	public void createParser_invalid_args() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(TestParser.class, String.class).withArgs(4711));
	}
	
	@Test
	public void createParser_invalid_noDefaultCtor() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(NoDefaultCtorTestParser.class));
	}
	
	@Test
	public void createParser_invalid_finalRuleMethod() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(FinalRuleMethodTestParser.class));
	}
	
	@Test
	public void createParser_invalid_staticRuleMethod() {
		assertThrows(
				ParserCreateException.class,
				() -> Grampa.createParser(StaticRuleMethodTestParser.class));
	}
	
	@Test
	public void createParser_valid_inheritance() {
		final SuperParser superParser = Grampa.createParser(SuperParser.class);
		verifySuperParserRules(superParser.root());
		
		final SubParser subParser = Grampa.createParser(SubParser.class);
		verifySuperParserRules(subParser.root());
	}
	
	private void verifyTestParserRules(final Rule<String> root) {
		assertTrue(root instanceof FirstOfRule);
		assertEquals(2, root.getChildren().size());
		assertTrue(root.getChildren().get(0) instanceof CharPredicateRule);
		assertTrue(root.getChildren().get(1) instanceof SequenceRule);
		
		final SequenceRule<String> sequenceRule = (SequenceRule<String>) root.getChildren().get(1);
		assertEquals(6, sequenceRule.getChildren().size());
		assertTrue(sequenceRule.getChildren().get(0) instanceof EmptyRule);
		assertTrue(sequenceRule.getChildren().get(1) instanceof EmptyRule);
		assertTrue(sequenceRule.getChildren().get(2) instanceof EmptyRule);
		assertTrue(sequenceRule.getChildren().get(3) instanceof CharPredicateRule);
		assertTrue(sequenceRule.getChildren().get(4) instanceof FirstOfRule);
		assertTrue(sequenceRule.getChildren().get(5) instanceof CharPredicateRule);
		
		assertEquals(root, sequenceRule.getChildren().get(4));
	}
	
	private void verifySuperParserRules(final Rule<String> root) {
		assertTrue(root instanceof SequenceRule);
		assertEquals(2, root.getChildren().size());
		assertTrue(root.getChildren().get(0) instanceof CharPredicateRule);
		assertTrue(root.getChildren().get(1) instanceof SequenceRule);
		
		assertEquals(root, root.getChildren().get(1));
	}
}

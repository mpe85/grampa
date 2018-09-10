package com.mpe85.grampa.runner;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.impl.StringRule;

public class ParseRunnerTest {
	
	@Test
	public void test_run() {
		final Parser<Void> parser = () -> new StringRule<>("foo");
		final DefaultParseRunner<Void> runner = new DefaultParseRunner<>(parser);
		assertTrue(runner.run("foo").isMatched());
		assertTrue(runner.run("foobar").isMatched());
		assertFalse(runner.run("bar").isMatched());
	}
	
}

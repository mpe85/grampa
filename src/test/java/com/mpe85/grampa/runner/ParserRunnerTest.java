package com.mpe85.grampa.runner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.impl.StringMatcher;

public class ParserRunnerTest {
	
	@Test
	public void test_run() {
		final IMatcher matcher = new StringMatcher("foo", false);
		final ParseRunner<Void> runner = new ParseRunner<>(matcher);
		Assertions.assertTrue(runner.run("foo"));
		Assertions.assertTrue(runner.run("foobar"));
		Assertions.assertFalse(runner.run("bar"));
	}
	
}

package com.mpe85.grampa.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.matcher.Action;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.impl.ActionMatcher;
import com.mpe85.grampa.matcher.impl.StringMatcher;

public class ParserRunnerTest {
	
	@Test
	public void test_run() {
		final IMatcher<Void> matcher = new StringMatcher<>("foo");
		final ParseRunner<Void> runner = new ParseRunner<>(matcher);
		assertTrue(runner.run("foo").matched);
		assertTrue(runner.run("foobar").matched);
		assertFalse(runner.run("bar").matched);
	}
	
	@Test
	public void test_run2() {
		final Action<Integer> action = ctx -> {
			ctx.getValueStack().push(4711);
			return true;
		};
		final IMatcher<Integer> matcher = new ActionMatcher<>(action);
		final ParseRunner<Integer> runner = new ParseRunner<>(matcher);
		assertEquals(Integer.valueOf(4711), runner.run("").getValueStackTop());
	}
	
}

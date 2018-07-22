package com.mpe85.grampa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.runner.ParseRunner;

public class AbstractParserTest {
	
	@Test
	public void test_action() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public IMatcher<Integer> root() {
				return action(ctx -> ctx.getValueStack().push(4711));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getValueStackTop());
	}
	
	@Test
	public void test_push() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public IMatcher<Integer> root() {
				return push(4711);
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getValueStackTop());
	}
	
}

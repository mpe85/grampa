package com.mpe85.grampa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.matcher.Rule;
import com.mpe85.grampa.runner.ParseResult;
import com.mpe85.grampa.runner.ParseRunner;

public class AbstractParserTest {
	
	@Test
	public void action() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return action(ctx -> ctx.getValueStack().push(4711));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getValueStackTop());
	}
	
	@Test
	public void push() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return push(4711);
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getValueStackTop());
	}
	
	@Test
	public void sequence_push() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						push(4711),
						push(ctx -> peek(ctx) + 4),
						sequence(
								push(ctx -> pop(1, ctx) + peek(ctx))),
						optional(action(ctx -> {
							ctx.getValueStack().push(0);
							return false;
						})));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertEquals(Integer.valueOf(9426), result.getValueStackTop());
		assertEquals(2, result.getValueStack().size());
		assertEquals(Integer.valueOf(9426), result.getValueStack().peek());
		assertEquals(Integer.valueOf(4715), result.getValueStack().peek(1));
	}
	
	@Test
	public void test_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						test(string("what")),
						string("whatever"));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
	}
	
	@Test
	public void test_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						test(string("ever")),
						string("whatever"));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
	@Test
	public void testNot_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						testNot(string("foo")),
						string("whatever"));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
	}
	
	@Test
	public void testNot_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						testNot(string("what")),
						string("whatever"));
			}
		}
		final ParseRunner<Integer> runner = new ParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
}

package com.mpe85.grampa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.runner.ParseResult;
import com.mpe85.grampa.runner.DefaultParseRunner;

public class AbstractParserTest {
	
	@Test
	public void action() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return action(ctx -> ctx.getValueStack().push(4711));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
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
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
	@Test
	public void trie_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return trie("foo", "football", "foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fooba");
		assertTrue(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
	@Test
	public void previousMatch_valid() {
		final class Parser extends AbstractParser<CharSequence> {
			@Override
			public Rule<CharSequence> root() {
				return sequence(
						string("hello"),
						string("world"),
						push(ctx -> ctx.getParent().getPreviousMatch()),
						sequence(
								string("foo"),
								string("bar")),
						push(ctx -> ctx.getParent().getPreviousMatch()),
						test(string("baz")),
						push(ctx -> ctx.getParent().getPreviousMatch()),
						sequence(
								test(string("ba")),
								string("b"),
								test(string("az"))),
						push(ctx -> ctx.getParent().getPreviousMatch()));
			}
		}
		final DefaultParseRunner<CharSequence> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<CharSequence> result = runner.run("helloworldfoobarbaz");
		assertTrue(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertEquals("b", result.getValueStack().pop());
		assertEquals("foobar", result.getValueStack().pop());
		assertEquals("foobar", result.getValueStack().pop());
		assertEquals("world", result.getValueStackTop());
	}
	
}

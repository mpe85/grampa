package com.mpe85.grampa.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.runner.DefaultParseRunner;
import com.mpe85.grampa.runner.ParseResult;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "SIC_INNER_SHOULD_BE_STATIC_ANON", justification = "Performance is not of great importance in unit tests.")
public class AbstractParserTest {
	
	@Test
	public void empty_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return empty();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foo");
		assertTrue(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertEquals("", result.getMatchedInput());
		assertEquals("foo", result.getRestOfInput());
	}
	
	@Test
	public void never_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return never();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foo");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("foo", result.getRestOfInput());
	}
	
	@Test
	public void eoi_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(string("foo"), eoi());
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foo");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("foo", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void eoi_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(string("foo"), eoi());
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foo ");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("foo ", result.getRestOfInput());
	}
	
	@Test
	public void anyChar_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyChar();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("f");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("f", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void anyChar_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyChar();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void anyCodePoint_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyCodePoint();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\uD835\uDD38");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("\uD835\uDD38", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void anyCodePoint_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyCodePoint();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void action() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return command(ctx -> ctx.getStack().push(4711));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getStackTop());
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
		assertEquals(Integer.valueOf(4711), runner.run("whatever").getStackTop());
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
							ctx.getStack().push(0);
							return false;
						})));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("whatever");
		assertEquals(Integer.valueOf(9426), result.getStackTop());
		assertEquals(2, result.getStack().size());
		assertEquals(Integer.valueOf(9426), result.getStack().peek());
		assertEquals(Integer.valueOf(4715), result.getStack().peek(1));
	}
	
	
	@Test
	public void firstOf() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						firstOf(
								sequence(string("foo"), string("bar")),
								sequence(string("foo"), string("baz"))),
						string("xxx"));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foobazxxx");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
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
	public void strings_valid() {
		final AtomicReference<CharSequence> stringsRuleMatch = new AtomicReference<>();
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						strings("football", "foo", "foobar"),
						command(ctx -> stringsRuleMatch.set(ctx.getPreviousMatch())),
						string("baz"));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foobaz");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("foo", stringsRuleMatch.get());
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
		assertEquals("b", result.getStack().pop());
		assertEquals("foobar", result.getStack().pop());
		assertEquals("foobar", result.getStack().pop());
		assertEquals("world", result.getStackTop());
	}
	
	@Test
	public void repeat_valid_times() {
		final class Parser extends AbstractParser<CharSequence> {
			@Override
			public Rule<CharSequence> root() {
				return repeat(character('z')).times(4, 7);
			}
		}
		final DefaultParseRunner<CharSequence> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<CharSequence> result = runner.run("zzzzz");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
	}
	
	@Test
	public void repeat_invalid_times() {
		final class Parser extends AbstractParser<CharSequence> {
			@Override
			public Rule<CharSequence> root() {
				return repeat(character('z')).times(6, 7);
			}
		}
		final DefaultParseRunner<CharSequence> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<CharSequence> result = runner.run("zzzzz");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
	@Test
	public void repeat_valid_min() {
		final class Parser extends AbstractParser<CharSequence> {
			@Override
			public Rule<CharSequence> root() {
				return repeat(character('z')).min(3);
			}
		}
		final DefaultParseRunner<CharSequence> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<CharSequence> result = runner.run("zzzzz");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
	}
	
	@Test
	public void repeat_invalid_min() {
		final class Parser extends AbstractParser<CharSequence> {
			@Override
			public Rule<CharSequence> root() {
				return repeat(character('z')).min(8);
			}
		}
		final DefaultParseRunner<CharSequence> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<CharSequence> result = runner.run("zzzzz");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
	}
	
}

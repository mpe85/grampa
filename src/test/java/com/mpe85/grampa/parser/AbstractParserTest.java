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
	public void character_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return character('f');
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
	public void character_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return character('f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("g");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("g", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_character_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase('f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("F");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("F", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_character_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase('f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("G");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("G", result.getRestOfInput());
	}
	
	@Test
	public void charRange_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return charRange('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("c");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("c", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void charRange_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return charRange('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("h");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("h", result.getRestOfInput());
	}
	
	@Test
	public void anyOfChars_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyOfChars('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("a");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("a", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void anyOfChars_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyOfChars('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("c");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("c", result.getRestOfInput());
	}
	
	@Test
	public void noneOfChars_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return noneOfChars('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("c");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("c", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void noneOfChars_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return noneOfChars('a', 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("f");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("f", result.getRestOfInput());
	}
	
	@Test
	public void codePoint_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return codePoint("\uD835\uDD38".codePointAt(0));
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
	public void codePoint_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return codePoint("\uD835\uDD38".codePointAt(0));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\uD835\uDD39");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\uD835\uDD39", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_codePoint_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase((int) 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("F");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("F", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_codePoint_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase((int) 'f');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("G");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("G", result.getRestOfInput());
	}
	
	@Test
	public void codePointRange_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return codePointRange('Z', 'b');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("a");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("a", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void codePointRange_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return codePointRange('Z', 'b');
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("X");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("X", result.getRestOfInput());
	}
	
	@Test
	public void anyOfCodePoint_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyOfCodePoints('a', "\uD835\uDD38".codePointAt(0));
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
	public void anyOfCodePoints_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return anyOfCodePoints('a', "\uD835\uDD38".codePointAt(0));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("b");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("b", result.getRestOfInput());
	}
	
	@Test
	public void noneOfCodePoints_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return noneOfCodePoints('a', "\uD835\uDD38".codePointAt(0));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("b");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("b", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void noneOfCodePoints_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return noneOfCodePoints('a', "\uD835\uDD38".codePointAt(0));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\uD835\uDD38");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\uD835\uDD38", result.getRestOfInput());
	}
	
	@Test
	public void string_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return string("foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foobart");
		assertTrue(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertEquals("foobar", result.getMatchedInput());
		assertEquals("t", result.getRestOfInput());
	}
	
	@Test
	public void string_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return string("foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("foobär");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("foobär", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_string_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase("foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fOObAr");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("fOObAr", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_string_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return string("fOObAr");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fOObÄr");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("fOObÄr", result.getRestOfInput());
	}
	
	@Test
	public void regex_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return regex("abc+");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("abcccccd");
		assertTrue(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertEquals("abccccc", result.getMatchedInput());
		assertEquals("d", result.getRestOfInput());
	}
	
	@Test
	public void regex_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return string("abc+");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("ab");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("ab", result.getRestOfInput());
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
		assertEquals("foobaz", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
		assertEquals("foo", stringsRuleMatch.get());
	}
	
	@Test
	public void strings_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return strings("football", "foo", "foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fo");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("fo", result.getRestOfInput());
	}
	
	@Test
	public void ignoreCase_strings_valid() {
		final AtomicReference<CharSequence> stringsRuleMatch = new AtomicReference<>();
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return sequence(
						ignoreCase("football", "foo", "foobar"),
						command(ctx -> stringsRuleMatch.set(ctx.getPreviousMatch())),
						string("baz"));
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fOObaz");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("fOObaz", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
		assertEquals("fOO", stringsRuleMatch.get());
	}
	
	@Test
	public void ignoreCase_strings_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ignoreCase("football", "foo", "foobar");
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("fO");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("fO", result.getRestOfInput());
	}
	
	@Test
	public void ascii_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ascii();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("#");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("#", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void ascii_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return ascii();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("ß");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("ß", result.getRestOfInput());
	}
	
	@Test
	public void bmp_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return bmp();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("ß");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("ß", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void bmp_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return bmp();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\uD835\uDD38");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\uD835\uDD38", result.getRestOfInput());
	}
	
	@Test
	public void digit_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return digit();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("5");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("5", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void digit_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return digit();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("O");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("O", result.getRestOfInput());
	}
	
	@Test
	public void javaIdentifierStart_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return javaIdentifierStart();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("ä");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("ä", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void javaIdentifierStart_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return javaIdentifierStart();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("1");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("1", result.getRestOfInput());
	}
	
	@Test
	public void javaIdentifierPart_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return javaIdentifierPart();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("1");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("1", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void javaIdentifierPart_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return javaIdentifierPart();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("(");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("(", result.getRestOfInput());
	}
	
	@Test
	public void letter_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return letter();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("Ü");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("Ü", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void letter_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return letter();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("$");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("$", result.getRestOfInput());
	}
	
	@Test
	public void letterOrDigit_valid_letter() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return letterOrDigit();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("x");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("x", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void letterOrDigit_valid_digit() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return letterOrDigit();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("9");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("9", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void letterOrDigit_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return letter();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("%");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("%", result.getRestOfInput());
	}
	
	@Test
	public void printable_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return printable();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("n");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("n", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void printable_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return printable();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\n", result.getRestOfInput());
	}
	
	@Test
	public void spaceChar_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return spaceChar();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run(" ");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals(" ", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void spaceChar_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return spaceChar();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\n", result.getRestOfInput());
	}
	
	@Test
	public void whitespace_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return whitespace();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("\n", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void whitespace_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return whitespace();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("_");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("_", result.getRestOfInput());
	}
	
	@Test
	public void cr_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return cr();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\r");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("\r", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void cr_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return cr();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\n", result.getRestOfInput());
	}
	
	@Test
	public void lf_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return lf();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("\n", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void lf_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return lf();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\r");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\r", result.getRestOfInput());
	}
	
	@Test
	public void crlf_valid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return crlf();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\r\n");
		assertTrue(result.isMatched());
		assertTrue(result.isMatchedWholeInput());
		assertEquals("\r\n", result.getMatchedInput());
		assertEquals("", result.getRestOfInput());
	}
	
	@Test
	public void crlf_invalid() {
		final class Parser extends AbstractParser<Integer> {
			@Override
			public Rule<Integer> root() {
				return crlf();
			}
		}
		final DefaultParseRunner<Integer> runner = new DefaultParseRunner<>(new Parser());
		final ParseResult<Integer> result = runner.run("\n\r");
		assertFalse(result.isMatched());
		assertFalse(result.isMatchedWholeInput());
		assertNull(result.getMatchedInput());
		assertEquals("\n\r", result.getRestOfInput());
	}
	
	@Test
	public void command_valid() {
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
	public void push_valid() {
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
	public void sequence_push_valid() {
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
	public void firstOf_valid() {
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

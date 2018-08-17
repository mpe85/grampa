package com.mpe85.grampa.parser;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Sets;
import com.ibm.icu.lang.UCharacter;
import com.mpe85.grampa.builder.RepeatRuleBuilder;
import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.ActionContext;
import com.mpe85.grampa.rule.Command;
import com.mpe85.grampa.rule.EventSupplier;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.ValueSupplier;
import com.mpe85.grampa.rule.impl.ActionRule;
import com.mpe85.grampa.rule.impl.CharPredicateRule;
import com.mpe85.grampa.rule.impl.CodePointPredicateRule;
import com.mpe85.grampa.rule.impl.EmptyRule;
import com.mpe85.grampa.rule.impl.EndOfInputRule;
import com.mpe85.grampa.rule.impl.FirstOfRule;
import com.mpe85.grampa.rule.impl.NeverRule;
import com.mpe85.grampa.rule.impl.RegexRule;
import com.mpe85.grampa.rule.impl.SequenceRule;
import com.mpe85.grampa.rule.impl.StringRule;
import com.mpe85.grampa.rule.impl.TestNotRule;
import com.mpe85.grampa.rule.impl.TestRule;
import com.mpe85.grampa.rule.impl.TrieRule;

import one.util.streamex.IntStreamEx;
import one.util.streamex.StreamEx;

public abstract class AbstractParser<T> implements Parser<T> {
	
	@Override
	public abstract Rule<T> root();
	
	protected Rule<T> empty() {
		return EMPTY;
	}
	
	protected Rule<T> never() {
		return NEVER;
	}
	
	protected Rule<T> eoi() {
		return EOI;
	}
	
	protected Rule<T> anyChar() {
		return ANY_CHAR;
	}
	
	protected Rule<T> anyCodePoint() {
		return ANY_CODEPOINT;
	}
	
	protected Rule<T> character(final char character) {
		return new CharPredicateRule<>(CharMatcher.is(character));
	}
	
	protected Rule<T> ignoreCase(final char character) {
		return new CharPredicateRule<>(
				CharMatcher.is(Character.toLowerCase(character))
						.or(CharMatcher.is(Character.toUpperCase(character))));
	}
	
	protected Rule<T> charRange(final char lowerBound, final char upperBound) {
		return new CharPredicateRule<>(CharMatcher.inRange(lowerBound, upperBound));
	}
	
	protected Rule<T> anyOfChars(final char... characters) {
		return anyOfChars(String.valueOf(checkNotNull(characters, "A 'characters' array must not be null.")));
	}
	
	protected Rule<T> anyOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return anyOfChars(StreamEx.of(characters).joining());
	}
	
	protected Rule<T> anyOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
		if (characters.length() == 0) {
			return NEVER;
		}
		else if (characters.length() == 1) {
			return character(characters.charAt(0));
		}
		return new CharPredicateRule<>(CharMatcher.anyOf(characters));
	}
	
	protected Rule<T> noneOfChars(final char... characters) {
		return noneOfChars(String.valueOf(checkNotNull(characters, "A 'characters' array must not be null.")));
	}
	
	protected Rule<T> noneOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return noneOfChars(StreamEx.of(characters).joining());
	}
	
	protected Rule<T> noneOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
		if (characters.length() == 0) {
			return ANY_CHAR;
		}
		return new CharPredicateRule<>(CharMatcher.noneOf(characters));
	}
	
	protected Rule<T> codePoint(final int codePoint) {
		return new CodePointPredicateRule<>(cp -> cp == codePoint);
	}
	
	protected Rule<T> ignoreCase(final int codePoint) {
		return new CodePointPredicateRule<>(
				cp -> cp == UCharacter.toLowerCase(codePoint) || cp == UCharacter.toUpperCase(codePoint));
	}
	
	protected Rule<T> codePointRange(final int lowerBound, final int upperBound) {
		checkArgument(lowerBound <= upperBound, "A 'lowerBound' must not be greater than an 'upperBound'.");
		return new CodePointPredicateRule<>(cp -> cp >= lowerBound && cp <= upperBound);
	}
	
	protected Rule<T> anyOfCodePoints(final int... codePoints) {
		checkNotNull(codePoints, "A 'codePoints' array must not be null.");
		if (codePoints.length == 0) {
			return NEVER;
		}
		else if (codePoints.length == 1) {
			return codePoint(codePoints[0]);
		}
		Arrays.sort(codePoints);
		return new CodePointPredicateRule<>(cp -> Arrays.binarySearch(codePoints, cp) >= 0);
	}
	
	protected Rule<T> anyOfCodePoints(final String codePoints) {
		checkNotNull(codePoints, "A 'codePoints' string must not be null.");
		return anyOfCodePoints(codePoints.codePoints().toArray());
	}
	
	protected Rule<T> anyOfCodePoints(final Set<Integer> codePoints) {
		checkNotNull(codePoints, "A set of 'codePoints' must not be null.");
		return anyOfCodePoints(IntStreamEx.of(codePoints).toArray());
	}
	
	protected Rule<T> noneOfCodePoints(final int... codePoints) {
		checkNotNull(codePoints, "A 'codePoints' array must not be null.");
		if (codePoints.length == 0) {
			return ANY_CODEPOINT;
		}
		Arrays.sort(codePoints);
		return new CodePointPredicateRule<>(cp -> Arrays.binarySearch(codePoints, cp) < 0);
	}
	
	protected Rule<T> noneOfCodePoints(final String codePoints) {
		checkNotNull(codePoints, "A 'codePoints' string must not be null.");
		return noneOfCodePoints(codePoints.codePoints().toArray());
	}
	
	protected Rule<T> noneOfCodePoints(final Set<Integer> codePoints) {
		checkNotNull(codePoints, "A set of 'codePoints' must not be null.");
		return noneOfCodePoints(IntStreamEx.of(codePoints).toArray());
	}
	
	protected Rule<T> string(final String string) {
		checkNotNull(string, "A 'string' must not be null.");
		if (string.length() == 0) {
			return EMPTY;
		}
		else if (string.length() == 1) {
			return character(string.charAt(0));
		}
		return new StringRule<>(string);
	}
	
	protected Rule<T> ignoreCase(final String string) {
		checkNotNull(string, "A 'string' must not be null.");
		if (string.length() == 0) {
			return EMPTY;
		}
		else if (string.length() == 1) {
			return ignoreCase(string.charAt(0));
		}
		return new StringRule<>(string, true);
		
	}
	
	protected Rule<T> regex(final String regex) {
		return new RegexRule<>(checkNotNull(regex, "A 'regex' must not be null."));
	}
	
	protected Rule<T> strings(final String... strings) {
		return strings(Sets.newHashSet(checkNotNull(strings, "A set of 'strings' must not be null.")));
	}
	
	protected Rule<T> strings(final Set<String> strings) {
		checkNotNull(strings, "A set of 'strings' must not be null.");
		if (strings.size() == 0) {
			return NEVER;
		}
		else if (strings.size() == 1) {
			return string(strings.iterator().next());
		}
		return new TrieRule<>(strings);
	}
	
	protected Rule<T> ignoreCase(final String... strings) {
		return ignoreCase(Sets.newHashSet(checkNotNull(strings, "A set of 'strings' must not be null.")));
	}
	
	protected Rule<T> ignoreCase(final Set<String> strings) {
		checkNotNull(strings, "A set of 'strings' must not be null.");
		if (strings.size() == 0) {
			return NEVER;
		}
		else if (strings.size() == 1) {
			return ignoreCase(strings.iterator().next());
		}
		return new TrieRule<>(strings, true);
	}
	
	protected Rule<T> ascii() {
		return ASCII;
	}
	
	protected Rule<T> bmp() {
		return BMP;
	}
	
	protected Rule<T> digit() {
		return DIGIT;
	}
	
	protected Rule<T> javaIdentifierStart() {
		return JAVA_IDENTIFIER_START;
	}
	
	protected Rule<T> javaIdentifierPart() {
		return JAVA_IDENTIFIER_PART;
	}
	
	protected Rule<T> letter() {
		return LETTER;
	}
	
	protected Rule<T> letterOrDigit() {
		return LETTER_OR_DIGIT;
	}
	
	protected Rule<T> printable() {
		return PRINTABLE;
	}
	
	protected Rule<T> spaceChar() {
		return SPACE_CHAR;
	}
	
	protected Rule<T> whitespace() {
		return WHITESPACE;
	}
	
	protected Rule<T> cr() {
		return CR;
	}
	
	protected Rule<T> lf() {
		return LF;
	}
	
	protected Rule<T> crlf() {
		return CRLF;
	}
	
	@SafeVarargs
	protected final Rule<T> sequence(final Rule<T>... rules) {
		return sequence(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	protected Rule<T> sequence(final List<Rule<T>> rules) {
		checkNotNull(rules, "A 'rules' list must not be null.");
		if (rules.size() == 0) {
			return EMPTY;
		}
		else if (rules.size() == 1) {
			return rules.get(0);
		}
		return new SequenceRule<>(rules);
	}
	
	@SafeVarargs
	protected final Rule<T> firstOf(final Rule<T>... rules) {
		return firstOf(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	protected Rule<T> firstOf(final List<Rule<T>> rules) {
		checkNotNull(rules, "A 'rules' list must not be null.");
		if (rules.size() == 0) {
			return EMPTY;
		}
		else if (rules.size() == 1) {
			return rules.get(0);
		}
		return new FirstOfRule<>(rules);
	}
	
	protected Rule<T> optional(final Rule<T> rule) {
		return repeat(rule).times(0, 1);
	}
	
	protected Rule<T> zeroOrMore(final Rule<T> rule) {
		return repeat(rule).min(0);
	}
	
	protected Rule<T> oneOrMore(final Rule<T> rule) {
		return repeat(rule).min(1);
	}
	
	protected RepeatRuleBuilder<T> repeat(final Rule<T> rule) {
		return new RepeatRuleBuilder<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected Rule<T> test(final Rule<T> rule) {
		return new TestRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected Rule<T> testNot(final Rule<T> rule) {
		return new TestNotRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected Rule<T> action(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."));
	}
	
	protected Rule<T> command(final Command<T> command) {
		checkNotNull(command, "A 'command' must not be null.");
		return action(command.toAction());
	}
	
	protected Rule<T> skippableAction(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."), true);
	}
	
	protected Rule<T> skippableCommand(final Command<T> command) {
		checkNotNull(command, "A 'command' must not be null.");
		return skippableAction(command.toAction());
	}
	
	protected Rule<T> post(final Object event) {
		checkNotNull(event, "An 'event' must not be null.");
		return skippableCommand(ctx -> ctx.post(event));
	}
	
	protected Rule<T> post(final EventSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return skippableCommand(ctx -> ctx.post(supplier.supply(ctx)));
	}
	
	protected Rule<T> pop() {
		return action(ctx -> {
			ctx.getStack().pop();
			return true;
		});
	}
	
	protected Rule<T> poke(final T value) {
		return action(ctx -> {
			ctx.getStack().poke(value);
			return true;
		});
	}
	
	protected Rule<T> poke(final int down, final T value) {
		return action(ctx -> {
			ctx.getStack().poke(down, value);
			return true;
		});
	}
	
	protected Rule<T> poke(final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return action(ctx -> {
			ctx.getStack().poke(supplier.supply(ctx));
			return true;
		});
	}
	
	protected Rule<T> poke(final int down, final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return action(ctx -> {
			ctx.getStack().poke(down, supplier.supply(ctx));
			return true;
		});
	}
	
	protected Rule<T> push(final T value) {
		return command(ctx -> ctx.getStack().push(value));
	}
	
	protected Rule<T> push(final ValueSupplier<T> supplier) {
		checkNotNull(supplier, "A 'supplier' must not be null.");
		return command(ctx -> ctx.getStack().push(supplier.supply(ctx)));
	}
	
	protected Rule<T> dup() {
		return command(ctx -> ctx.getStack().dup());
	}
	
	protected Rule<T> swap() {
		return command(ctx -> ctx.getStack().swap());
	}
	
	protected final T pop(final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().pop();
	}
	
	protected final T pop(final int down, final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().pop(down);
	}
	
	protected final <U extends T> U popAs(final Class<U> clazz, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return clazz.cast(context.getStack().pop());
	}
	
	protected final <U extends T> U popAs(final Class<U> clazz, final int down, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return clazz.cast(context.getStack().pop(down));
	}
	
	protected final T peek(final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peek();
	}
	
	protected final T peek(final int down, final ActionContext<T> context) {
		checkNotNull(context, "A 'context' must not be null.");
		return context.getStack().peek(down);
	}
	
	protected final <U extends T> U peekAs(final Class<U> clazz, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return clazz.cast(context.getStack().peek());
	}
	
	protected final <U extends T> U peekAs(final Class<U> clazz, final int down, final ActionContext<T> context) {
		checkNotNull(clazz, "A 'clazz' must not be null.");
		checkNotNull(context, "A 'context' must not be null.");
		return clazz.cast(context.getStack().peek(down));
	}
	
	
	protected final Rule<T> EMPTY = new EmptyRule<>();
	protected final Rule<T> NEVER = new NeverRule<>();
	protected final Rule<T> EOI = new EndOfInputRule<>();
	protected final Rule<T> ANY_CHAR = new CharPredicateRule<>(CharMatcher.any());
	protected final Rule<T> ANY_CODEPOINT = new CodePointPredicateRule<>(UCharacter::isLegal);
	protected final Rule<T> ASCII = new CharPredicateRule<>(CharMatcher.ascii());
	protected final Rule<T> BMP = new CodePointPredicateRule<>(UCharacter::isBMP);
	protected final Rule<T> DIGIT = new CodePointPredicateRule<>(UCharacter::isDigit);
	protected final Rule<T> JAVA_IDENTIFIER_START = new CodePointPredicateRule<>(Character::isJavaIdentifierStart);
	protected final Rule<T> JAVA_IDENTIFIER_PART = new CodePointPredicateRule<>(Character::isJavaIdentifierPart);
	protected final Rule<T> LETTER = new CodePointPredicateRule<>(UCharacter::isLetter);
	protected final Rule<T> LETTER_OR_DIGIT = new CodePointPredicateRule<>(UCharacter::isLetterOrDigit);
	protected final Rule<T> PRINTABLE = new CodePointPredicateRule<>(UCharacter::isPrintable);
	protected final Rule<T> SPACE_CHAR = new CodePointPredicateRule<>(UCharacter::isSpaceChar);
	protected final Rule<T> WHITESPACE = new CodePointPredicateRule<>(UCharacter::isWhitespace);
	protected final Rule<T> CR = new CharPredicateRule<>('\r');
	protected final Rule<T> LF = new CharPredicateRule<>('\n');
	protected final Rule<T> CRLF = new StringRule<>("\r\n");
	
}

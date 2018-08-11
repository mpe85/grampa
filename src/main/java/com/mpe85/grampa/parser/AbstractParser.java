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
import com.mpe85.grampa.rule.AlwaysSuccessingAction;
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
		return anyOfChars(String.valueOf(characters));
	}
	
	protected Rule<T> anyOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return anyOfChars(StreamEx.of(characters).joining());
	}
	
	protected Rule<T> anyOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
		return new CharPredicateRule<>(CharMatcher.anyOf(characters));
	}
	
	protected Rule<T> noneOfChars(final char... characters) {
		return noneOfChars(String.valueOf(characters));
	}
	
	protected Rule<T> noneOfChars(final Set<Character> characters) {
		checkNotNull(characters, "A set of 'characters' must not be null.");
		return noneOfChars(StreamEx.of(characters).joining());
	}
	
	protected Rule<T> noneOfChars(final String characters) {
		checkNotNull(characters, "A 'characters' string must not be null.");
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
		switch (checkNotNull(string, "A 'string' must not be null.").length()) {
			case 0:
				return EMPTY;
			case 1:
				return character(string.charAt(0));
			default:
				return new StringRule<>(string);
		}
	}
	
	protected Rule<T> stringIgnoreCase(final String string) {
		switch (checkNotNull(string, "A 'string' must not be null.").length()) {
			case 0:
				return EMPTY;
			case 1:
				return ignoreCase(string.charAt(0));
			default:
				return new StringRule<>(string, true);
		}
	}
	
	protected Rule<T> regex(final String regex) {
		return new RegexRule<>(checkNotNull(regex, "A 'regex' must not be null."));
	}
	
	protected Rule<T> strings(final String... strings) {
		return strings(Sets.newHashSet(strings));
	}
	
	protected Rule<T> strings(final Set<String> strings) {
		switch (checkNotNull(strings, "A set of 'strings' must not be null.").size()) {
			case 0:
				return NEVER;
			case 1:
				return new StringRule<>(strings.iterator().next());
			default:
				return new TrieRule<>(strings);
		}
	}
	
	protected Rule<T> stringsIgnoreCase(final String... strings) {
		return stringsIgnoreCase(Sets.newHashSet(strings));
	}
	
	protected Rule<T> stringsIgnoreCase(final Set<String> strings) {
		switch (checkNotNull(strings, "A set of 'strings' must not be null.").size()) {
			case 0:
				return NEVER;
			case 1:
				return new StringRule<>(strings.iterator().next(), true);
			default:
				return new TrieRule<>(strings, true);
		}
	}
	
	@SafeVarargs
	protected final Rule<T> sequence(final Rule<T>... rules) {
		return sequence(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	protected Rule<T> sequence(final List<Rule<T>> rules) {
		switch (checkNotNull(rules, "A 'rules' list must not be null.").size()) {
			case 0:
				return EMPTY;
			case 1:
				return rules.get(0);
			default:
				return new SequenceRule<>(rules);
		}
	}
	
	@SafeVarargs
	protected final Rule<T> firstOf(final Rule<T>... rules) {
		return firstOf(Arrays.asList(checkNotNull(rules, "A 'rules' array must not be null.")));
	}
	
	protected Rule<T> firstOf(final List<Rule<T>> rules) {
		switch (checkNotNull(rules, "A 'rules' list must not be null.").size()) {
			case 0:
				return NEVER;
			case 1:
				return rules.get(0);
			default:
				return new FirstOfRule<>(rules);
		}
	}
	
	protected Rule<T> optional(final Rule<T> rule) {
		return repeat(rule).times(0, 1);
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
	
	protected Rule<T> action(final AlwaysSuccessingAction<T> action) {
		checkNotNull(action, "An 'action' must not be null.");
		return action(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected Rule<T> skippableAction(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."), true);
	}
	
	protected Rule<T> skippableAction(final AlwaysSuccessingAction<T> action) {
		checkNotNull(action, "An 'action' must not be null.");
		return skippableAction(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected Rule<T> pop() {
		return new ActionRule<>(ctx -> {
			ctx.getStack().pop();
			return true;
		});
	}
	
	protected final T pop(final ActionContext<T> context) {
		return context.getStack().pop();
	}
	
	protected final T pop(final int down, final ActionContext<T> context) {
		return context.getStack().pop(down);
	}
	
	protected final T peek(final ActionContext<T> context) {
		return context.getStack().peek();
	}
	
	protected final T peek(final int down, final ActionContext<T> context) {
		return context.getStack().peek(down);
	}
	
	protected Rule<T> push(final T value) {
		return new ActionRule<>(ctx -> {
			ctx.getStack().push(value);
			return true;
		});
	}
	
	protected Rule<T> push(final ValueSupplier<T> supplier) {
		return new ActionRule<>(ctx -> {
			ctx.getStack().push(supplier.supply(ctx));
			return true;
		});
	}
	
	
	protected final Rule<T> EMPTY = new EmptyRule<>();
	protected final Rule<T> NEVER = new NeverRule<>();
	protected final Rule<T> EOI = new EndOfInputRule<>();
	protected final Rule<T> ANY_CHAR = new CharPredicateRule<>(CharMatcher.any());
	protected final Rule<T> ANY_CODEPOINT = new CodePointPredicateRule<>(UCharacter::isLegal);
	
}

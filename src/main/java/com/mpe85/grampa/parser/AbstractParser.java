package com.mpe85.grampa.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Ints;
import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.ActionContext;
import com.mpe85.grampa.rule.AlwaysSuccessingAction;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.ValueSupplier;
import com.mpe85.grampa.rule.impl.ActionRule;
import com.mpe85.grampa.rule.impl.AnyCharRule;
import com.mpe85.grampa.rule.impl.AnyCodePointRule;
import com.mpe85.grampa.rule.impl.AnyOfCharsRule;
import com.mpe85.grampa.rule.impl.AnyOfCodePointsRule;
import com.mpe85.grampa.rule.impl.CharRangeRule;
import com.mpe85.grampa.rule.impl.CharRule;
import com.mpe85.grampa.rule.impl.CodePointRangeRule;
import com.mpe85.grampa.rule.impl.CodePointRule;
import com.mpe85.grampa.rule.impl.EmptyRule;
import com.mpe85.grampa.rule.impl.EndOfInputRule;
import com.mpe85.grampa.rule.impl.FirstOfRule;
import com.mpe85.grampa.rule.impl.NeverRule;
import com.mpe85.grampa.rule.impl.OptionalRule;
import com.mpe85.grampa.rule.impl.RegexRule;
import com.mpe85.grampa.rule.impl.SequenceRule;
import com.mpe85.grampa.rule.impl.StringRule;
import com.mpe85.grampa.rule.impl.StringsRule;
import com.mpe85.grampa.rule.impl.TestNotRule;
import com.mpe85.grampa.rule.impl.TestRule;

import one.util.streamex.IntStreamEx;

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
		return new CharRule<>(character);
	}
	
	protected Rule<T> ignoreCase(final char character) {
		return new CharRule<>(character, true);
	}
	
	protected Rule<T> charRange(final char lowerBound, final char upperBound) {
		return lowerBound == upperBound
				? new CharRule<>(lowerBound)
				: new CharRangeRule<>(lowerBound, upperBound);
	}
	
	protected Rule<T> anyOfChars(final char... characters) {
		return anyOfChars(Sets.newHashSet(Chars.asList(characters)));
	}
	
	protected Rule<T> anyOfChars(final String characters) {
		return anyOfChars(IntStreamEx.ofChars(checkNotNull(characters, "A 'characters' string must not be null."))
				.mapToObj(Character.class::cast)
				.toSet());
	}
	
	protected Rule<T> anyOfChars(final Set<Character> characters) {
		switch (checkNotNull(characters, "A set of 'characters' must not be null.").size()) {
			case 0:
				return NEVER;
			case 1:
				return new CharRule<>(characters.iterator().next());
			default:
				return new AnyOfCharsRule<>(characters);
		}
	}
	
	protected Rule<T> noneOfChars(final char... characters) {
		return noneOfChars(Sets.newHashSet(Chars.asList(characters)));
	}
	
	protected Rule<T> noneOfChars(final String characters) {
		return noneOfChars(IntStreamEx.ofChars(checkNotNull(characters, "A 'characters' string must not be null."))
				.mapToObj(Character.class::cast)
				.toSet());
	}
	
	protected Rule<T> noneOfChars(final Set<Character> characters) {
		switch (checkNotNull(characters, "A set of 'characters' must not be null.").size()) {
			case 0:
				return ANY_CHAR;
			default:
				return new AnyOfCharsRule<>(characters, true);
		}
	}
	
	protected Rule<T> codePoint(final int codePoint) {
		return new CodePointRule<>(codePoint);
	}
	
	protected Rule<T> ignoreCase(final int codePoint) {
		return new CodePointRule<>(codePoint, true);
	}
	
	protected Rule<T> codePointRange(final int lowerBound, final int upperBound) {
		return lowerBound == upperBound
				? new CodePointRule<>(lowerBound)
				: new CodePointRangeRule<>(lowerBound, upperBound);
	}
	
	protected Rule<T> anyOfCodePoints(final int... codePoints) {
		return anyOfCodePoints(Sets.newHashSet(Ints.asList(codePoints)));
	}
	
	protected Rule<T> anyOfCodePoints(final String codePoints) {
		return anyOfCodePoints(
				IntStreamEx.ofCodePoints(checkNotNull(codePoints, "A 'codePoints' string must not be null."))
						.mapToObj(Integer.class::cast)
						.toSet());
	}
	
	protected Rule<T> anyOfCodePoints(final Set<Integer> codePoints) {
		switch (checkNotNull(codePoints, "A set of 'codePoints' must not be null.").size()) {
			case 0:
				return NEVER;
			case 1:
				return new CodePointRule<>(codePoints.iterator().next());
			default:
				return new AnyOfCodePointsRule<>(codePoints);
		}
	}
	
	protected Rule<T> noneOfCodePoints(final int... codePoints) {
		return noneOfCodePoints(Sets.newHashSet(Ints.asList(codePoints)));
	}
	
	protected Rule<T> noneOfCodePoints(final String codePoints) {
		return noneOfCodePoints(
				IntStreamEx.ofCodePoints(checkNotNull(codePoints, "A 'codePoints' string must not be null."))
						.mapToObj(Integer.class::cast)
						.toSet());
	}
	
	protected Rule<T> noneOfCodePoints(final Set<Integer> codePoints) {
		switch (checkNotNull(codePoints, "A set of 'codePoints' must not be null.").size()) {
			case 0:
				return ANY_CODEPOINT;
			default:
				return new AnyOfCodePointsRule<>(codePoints, true);
		}
	}
	
	protected Rule<T> string(final String string) {
		switch (checkNotNull(string, "A 'string' must not be null.").length()) {
			case 0:
				return EMPTY;
			case 1:
				return new CharRule<>(string.charAt(0));
			default:
				return new StringRule<>(string);
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
				return new StringsRule<>(strings);
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
		return new OptionalRule<>(checkNotNull(rule, "A 'rule' must not be null."));
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
	protected final Rule<T> ANY_CHAR = new AnyCharRule<>();
	protected final Rule<T> ANY_CODEPOINT = new AnyCodePointRule<>();
	
}

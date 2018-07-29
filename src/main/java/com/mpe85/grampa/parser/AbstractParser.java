package com.mpe85.grampa.parser;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.mpe85.grampa.rule.Action;
import com.mpe85.grampa.rule.AlwaysSuccessingAction;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.rule.ValueSupplier;
import com.mpe85.grampa.rule.impl.ActionRule;
import com.mpe85.grampa.rule.impl.AnyCharRule;
import com.mpe85.grampa.rule.impl.AnyCodePointRule;
import com.mpe85.grampa.rule.impl.CharRule;
import com.mpe85.grampa.rule.impl.CodePointRule;
import com.mpe85.grampa.rule.impl.EmptyRule;
import com.mpe85.grampa.rule.impl.EndOfInputRule;
import com.mpe85.grampa.rule.impl.NeverRule;
import com.mpe85.grampa.rule.impl.OptionalRule;
import com.mpe85.grampa.rule.impl.RegexRule;
import com.mpe85.grampa.rule.impl.SequenceRule;
import com.mpe85.grampa.rule.impl.StringRule;
import com.mpe85.grampa.rule.impl.TestNotRule;
import com.mpe85.grampa.rule.impl.TestRule;
import com.mpe85.grampa.rule.impl.TrieRule;

public abstract class AbstractParser<T> implements Parser<T> {
	
	@Override
	public abstract Rule<T> root();
	
	protected final Rule<T> empty() {
		return EMPTY;
	}
	
	protected final Rule<T> never() {
		return NEVER;
	}
	
	protected final Rule<T> eoi() {
		return EOI;
	}
	
	protected final Rule<T> anyChar() {
		return ANY_CHAR;
	}
	
	protected final Rule<T> anyCodePoint() {
		return ANY_CODEPOINT;
	}
	
	protected final Rule<T> character(final char character) {
		return new CharRule<>(character);
	}
	
	protected final Rule<T> codePoint(final int codePoint) {
		return new CodePointRule<>(codePoint);
	}
	
	protected final Rule<T> string(final String string) {
		switch (checkNotNull(string, "A 'string' must not be null.").length()) {
			case 0:
				return EMPTY;
			case 1:
				return new CharRule<>(string.charAt(0));
			default:
				return new StringRule<>(string);
		}
	}
	
	protected final Rule<T> regex(final String regex) {
		return new RegexRule<>(checkNotNull(regex, "A 'regex' must not be null."));
	}
	
	protected final Rule<T> trie(final String... strings) {
		switch (checkNotNull(strings, "A 'strings' array must not be null.").length) {
			case 0:
				return NEVER;
			case 1:
				return new StringRule<>(strings[0]);
			default:
				return new TrieRule<>(strings);
		}
	}
	
	@SafeVarargs
	protected final Rule<T> sequence(final Rule<T>... rules) {
		switch (checkNotNull(rules, "A 'rules' array must not be null.").length) {
			case 0:
				return EMPTY;
			case 1:
				return rules[0];
			default:
				return new SequenceRule<>(Arrays.asList(rules));
		}
	}
	
	protected final Rule<T> optional(final Rule<T> rule) {
		return new OptionalRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected final Rule<T> test(final Rule<T> rule) {
		return new TestRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected final Rule<T> testNot(final Rule<T> rule) {
		return new TestNotRule<>(checkNotNull(rule, "A 'rule' must not be null."));
	}
	
	protected final Rule<T> action(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."));
	}
	
	protected final Rule<T> action(final AlwaysSuccessingAction<T> action) {
		checkNotNull(action, "An 'action' must not be null.");
		return action(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected final Rule<T> skippableAction(final Action<T> action) {
		return new ActionRule<>(checkNotNull(action, "An 'action' must not be null."), true);
	}
	
	protected final Rule<T> skippableAction(final AlwaysSuccessingAction<T> action) {
		checkNotNull(action, "An 'action' must not be null.");
		return skippableAction(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected final Rule<T> pop() {
		return new ActionRule<>(ctx -> {
			ctx.getStack().pop();
			return true;
		});
	}
	
	protected final T pop(final RuleContext<T> context) {
		return context.getStack().pop();
	}
	
	protected final T pop(final int down, final RuleContext<T> context) {
		return context.getStack().pop(down);
	}
	
	protected final T peek(final RuleContext<T> context) {
		return context.getStack().peek();
	}
	
	protected final T peek(final int down, final RuleContext<T> context) {
		return context.getStack().peek(down);
	}
	
	protected final Rule<T> push(final T value) {
		return new ActionRule<>(ctx -> {
			ctx.getStack().push(value);
			return true;
		});
	}
	
	protected final Rule<T> push(final ValueSupplier<T> supplier) {
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

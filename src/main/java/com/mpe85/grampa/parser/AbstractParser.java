package com.mpe85.grampa.parser;

import java.util.Arrays;

import com.mpe85.grampa.matcher.Action;
import com.mpe85.grampa.matcher.AlwaysMatchingAction;
import com.mpe85.grampa.matcher.Rule;
import com.mpe85.grampa.matcher.RuleContext;
import com.mpe85.grampa.matcher.ValueSupplier;
import com.mpe85.grampa.matcher.impl.ActionRule;
import com.mpe85.grampa.matcher.impl.OptionaRule;
import com.mpe85.grampa.matcher.impl.SequenceRule;
import com.mpe85.grampa.matcher.impl.StringRule;
import com.mpe85.grampa.matcher.impl.TestNotRule;
import com.mpe85.grampa.matcher.impl.TestRule;

public abstract class AbstractParser<T> implements Parser<T> {
	
	@Override
	public abstract Rule<T> root();
	
	protected final Rule<T> string(final String string) {
		return new StringRule<>(string);
	}
	
	@SafeVarargs
	protected final Rule<T> sequence(final Rule<T>... rules) {
		return new SequenceRule<>(Arrays.asList(rules));
	}
	
	protected final Rule<T> optional(final Rule<T> rule) {
		return new OptionaRule<>(rule);
	}
	
	protected final Rule<T> test(final Rule<T> rule) {
		return new TestRule<>(rule);
	}
	
	protected final Rule<T> testNot(final Rule<T> rule) {
		return new TestNotRule<>(rule);
	}
	
	protected final Rule<T> action(final Action<T> action) {
		return new ActionRule<>(action);
	}
	
	protected final Rule<T> action(final AlwaysMatchingAction<T> action) {
		return action(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected final Rule<T> pop() {
		return new ActionRule<>(ctx -> {
			ctx.getValueStack().pop();
			return true;
		});
	}
	
	protected final T pop(final RuleContext<T> context) {
		return context.getValueStack().pop();
	}
	
	protected final T pop(final int down, final RuleContext<T> context) {
		return context.getValueStack().pop(down);
	}
	
	protected final T peek(final RuleContext<T> context) {
		return context.getValueStack().peek();
	}
	
	protected final T peek(final int down, final RuleContext<T> context) {
		return context.getValueStack().peek(down);
	}
	
	protected final Rule<T> push(final T value) {
		return new ActionRule<>(ctx -> {
			ctx.getValueStack().push(value);
			return true;
		});
	}
	
	protected final Rule<T> push(final ValueSupplier<T> supplier) {
		return new ActionRule<>(ctx -> {
			ctx.getValueStack().push(supplier.supply(ctx));
			return true;
		});
	}
	
}

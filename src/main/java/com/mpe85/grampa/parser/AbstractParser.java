package com.mpe85.grampa.parser;

import java.util.Arrays;

import com.mpe85.grampa.matcher.Action;
import com.mpe85.grampa.matcher.AlwaysMatchingAction;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.ValueSupplier;
import com.mpe85.grampa.matcher.impl.ActionMatcher;
import com.mpe85.grampa.matcher.impl.SequenceMatcher;
import com.mpe85.grampa.matcher.impl.StringMatcher;

public abstract class AbstractParser<T> implements IParser<T> {
	
	@Override
	public abstract IMatcher<T> root();
	
	protected final IMatcher<T> string(final String string) {
		return new StringMatcher<>(string);
	}
	
	@SafeVarargs
	protected final IMatcher<T> sequence(final IMatcher<T>... matchers) {
		return new SequenceMatcher<>(Arrays.asList(matchers));
	}
	
	protected final IMatcher<T> action(final Action<T> action) {
		return new ActionMatcher<>(action);
	}
	
	protected final IMatcher<T> action(final AlwaysMatchingAction<T> action) {
		return action(ctx -> {
			action.run(ctx);
			return true;
		});
	}
	
	protected final IMatcher<T> pop() {
		return new ActionMatcher<>(ctx -> {
			ctx.getValueStack().pop();
			return true;
		});
	}
	
	protected final IMatcher<T> push(final T value) {
		return new ActionMatcher<>(ctx -> {
			ctx.getValueStack().push(value);
			return true;
		});
	}
	
	protected final IMatcher<T> push(final ValueSupplier<T> supplier) {
		return new ActionMatcher<>(ctx -> {
			ctx.getValueStack().push(supplier.supply(ctx));
			return true;
		});
	}
	
}

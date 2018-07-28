package com.mpe85.grampa.runner;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.mpe85.grampa.event.ParseEventListener;
import com.mpe85.grampa.event.PostParseEvent;
import com.mpe85.grampa.event.PreParseEvent;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer;
import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.rule.impl.DefaultRuleContext;
import com.mpe85.grampa.util.stack.RestorableStack;
import com.mpe85.grampa.util.stack.impl.LinkedListRestorableStack;

public class ParseRunner<T> {
	
	public ParseRunner(final Parser<T> parser) {
		Preconditions.checkNotNull(parser, "A 'parser' must not be null.");
		this.rootRule = parser.root();
	}
	
	public Rule<T> getRootRule() {
		return rootRule;
	}
	
	public void registerListener(final ParseEventListener<T> listener) {
		bus.register(listener);
	}
	
	public void unregisterListener(final ParseEventListener<T> listener) {
		bus.unregister(listener);
	}
	
	public ParseResult<T> run(final CharSequence charSequence) {
		return run(new CharSequenceInputBuffer(charSequence));
	}
	
	public ParseResult<T> run(final InputBuffer inputBuffer) {
		Preconditions.checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		resetValueStack();
		final RuleContext<T> context = createRootContext(inputBuffer);
		
		bus.post(new PreParseEvent<>(context));
		
		final boolean matched = context.run();
		final ParseResult<T> result = new ParseResult<>(matched, context);
		
		bus.post(new PostParseEvent<>(result));
		
		return result;
	}
	
	protected RuleContext<T> createRootContext(final InputBuffer inputBuffer) {
		return new DefaultRuleContext<>(inputBuffer, 0, rootRule, 0, valueStack, bus);
	}
	
	private void resetValueStack() {
		valueStack = new LinkedListRestorableStack<>();
	}
	
	private final Rule<T> rootRule;
	private RestorableStack<T> valueStack;
	private final EventBus bus = new EventBus();
	
}

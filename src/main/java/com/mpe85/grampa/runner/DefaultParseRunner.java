package com.mpe85.grampa.runner;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.mpe85.grampa.event.ParseEventListener;
import com.mpe85.grampa.event.PostParseEvent;
import com.mpe85.grampa.event.PreParseEvent;
import com.mpe85.grampa.input.InputBuffer;
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer;
import com.mpe85.grampa.parser.Parser;
import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.RuleContext;
import com.mpe85.grampa.rule.impl.DefaultContext;
import com.mpe85.grampa.util.stack.RestorableStack;
import com.mpe85.grampa.util.stack.impl.LinkedListRestorableStack;

public class DefaultParseRunner<T> {
	
	public DefaultParseRunner(final Parser<T> parser) {
		this(parser, null);
	}
	
	public DefaultParseRunner(final Parser<T> parser, final SubscriberExceptionHandler handler) {
		checkNotNull(parser, "A 'parser' must not be null.");
		this.rootRule = parser.root();
		this.bus = handler != null ? new EventBus(handler) : new EventBus();
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
		checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		resetValueStack();
		final RuleContext<T> context = createRootContext(inputBuffer);
		
		bus.post(new PreParseEvent<>(context));
		
		final boolean matched = context.run();
		final ParseResult<T> result = new ParseResult<>(matched, context);
		
		bus.post(new PostParseEvent<>(result));
		
		return result;
	}
	
	protected RuleContext<T> createRootContext(final InputBuffer inputBuffer) {
		return new DefaultContext<>(inputBuffer, 0, rootRule, 0, valueStack, bus);
	}
	
	private void resetValueStack() {
		valueStack = new LinkedListRestorableStack<>();
	}
	
	
	private final Rule<T> rootRule;
	private final EventBus bus;
	private RestorableStack<T> valueStack;
	
}

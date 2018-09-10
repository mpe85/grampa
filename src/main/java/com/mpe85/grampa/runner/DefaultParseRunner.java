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

/**
 * The default parse runner. May be overridden by a custom implementation.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public class DefaultParseRunner<T> {
	
	/**
	 * C'tor.
	 * 
	 * @param parser
	 *            a parser instance
	 */
	public DefaultParseRunner(final Parser<T> parser) {
		this(parser, null);
	}
	
	/**
	 * C'tor.
	 * 
	 * @param parser
	 *            a parser instance
	 * @param handler
	 *            a handler for parser events
	 */
	public DefaultParseRunner(final Parser<T> parser, final SubscriberExceptionHandler handler) {
		checkNotNull(parser, "A 'parser' must not be null.");
		this.rootRule = parser.root();
		this.bus = handler != null ? new EventBus(handler) : new EventBus();
	}
	
	/**
	 * Gets the root rule of the parser.
	 * 
	 * @return the root rule
	 */
	public Rule<T> getRootRule() {
		return rootRule;
	}
	
	/**
	 * Registers a listener to the parser event bus.
	 * 
	 * @param listener
	 *            a parse event listener
	 */
	public void registerListener(final ParseEventListener<T> listener) {
		bus.register(listener);
	}
	
	/**
	 * Unregisters a listener to the parser event bus.
	 * 
	 * @param listener
	 *            a parse event listener
	 */
	public void unregisterListener(final ParseEventListener<T> listener) {
		bus.unregister(listener);
	}
	
	/**
	 * Runs the parser against a character sequence.
	 * 
	 * @param charSequence
	 *            a character sequence
	 * @return the parse result
	 */
	public ParseResult<T> run(final CharSequence charSequence) {
		return run(new CharSequenceInputBuffer(charSequence));
	}
	
	/**
	 * Runs the parser against an input buffer.
	 * 
	 * @param inputBuffer
	 *            an input buffer
	 * @return the parse result
	 */
	public ParseResult<T> run(final InputBuffer inputBuffer) {
		checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		resetStack();
		final RuleContext<T> context = createRootContext(inputBuffer);
		
		bus.post(new PreParseEvent<>(context));
		
		final boolean matched = context.run();
		final ParseResult<T> result = new ParseResult<>(matched, context);
		
		bus.post(new PostParseEvent<>(result));
		
		return result;
	}
	
	/**
	 * Creates the initial root context for the parser's root rule.
	 * 
	 * @param inputBuffer
	 *            an input buffer
	 * @return a rule context
	 */
	protected RuleContext<T> createRootContext(final InputBuffer inputBuffer) {
		return new DefaultContext<>(inputBuffer, 0, rootRule, 0, valueStack, bus);
	}
	
	/**
	 * Resets (clears) the stack.
	 */
	private void resetStack() {
		valueStack = new LinkedListRestorableStack<>();
	}
	
	
	private final Rule<T> rootRule;
	private final EventBus bus;
	private RestorableStack<T> valueStack;
	
}

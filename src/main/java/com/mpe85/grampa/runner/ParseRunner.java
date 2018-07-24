package com.mpe85.grampa.runner;

import com.google.common.base.Preconditions;
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
	
	public ParseResult<T> run(final CharSequence charSequence) {
		return run(new CharSequenceInputBuffer(charSequence));
	}
	
	public ParseResult<T> run(final InputBuffer inputBuffer) {
		Preconditions.checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		resetValueStack();
		final RuleContext<T> context = createRootContext(inputBuffer);
		
		final boolean matched = context.run();
		return new ParseResult<>(matched, context);
	}
	
	private RuleContext<T> createRootContext(final InputBuffer inputBuffer) {
		return new DefaultRuleContext<>(inputBuffer, 0, rootRule, 0, valueStack);
	}
	
	private void resetValueStack() {
		valueStack = new LinkedListRestorableStack<>();
	}
	
	private final Rule<T> rootRule;
	private RestorableStack<T> valueStack;
	
}

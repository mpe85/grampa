package com.mpe85.grampa.runner;

import com.google.common.base.Preconditions;
import com.mpe85.grampa.input.IInputBuffer;
import com.mpe85.grampa.input.impl.CharSequenceInputBuffer;
import com.mpe85.grampa.matcher.IMatcher;
import com.mpe85.grampa.matcher.IMatcherContext;
import com.mpe85.grampa.matcher.impl.DefaultMatcherContext;
import com.mpe85.grampa.util.stack.IRestorableStack;
import com.mpe85.grampa.util.stack.impl.RestorableStack;

public class ParseRunner<T> {
	
	public ParseRunner(final IMatcher rootMatcher) {
		this.rootMatcher = Preconditions.checkNotNull(rootMatcher, "A 'rootMatcher' must not be null.");
	}
	
	public IMatcher getRootMatcher() {
		return rootMatcher;
	}
	
	public boolean run(final CharSequence charSequence) {
		return run(new CharSequenceInputBuffer(charSequence));
	}
	
	public boolean run(final IInputBuffer inputBuffer) {
		Preconditions.checkNotNull(inputBuffer, "An 'inputBuffer' must not be null.");
		resetValueStack();
		final IMatcherContext<T> context = createRootContext(inputBuffer);
		
		final boolean matched = context.run();
		return matched;
	}
	
	private IMatcherContext<T> createRootContext(final IInputBuffer inputBuffer) {
		return new DefaultMatcherContext<>(inputBuffer, 0, rootMatcher, 0, valueStack);
	}
	
	private void resetValueStack() {
		valueStack = new RestorableStack<>();
	}
	
	private final IMatcher rootMatcher;
	private IRestorableStack<T> valueStack;
	
}

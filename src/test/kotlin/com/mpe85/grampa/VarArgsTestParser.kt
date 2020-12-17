package com.mpe85.grampa;

import java.util.List;

import com.mpe85.grampa.rule.Rule;

@SuppressWarnings("unchecked")
public class VarArgsTestParser extends TestParser {
	
	@SafeVarargs
	public final Rule<String> var1(final List<Object>... args) {
		return empty();
	}
	
	public final Rule<String> var2(final List<Object>... args) {
		return empty();
	}
	
}

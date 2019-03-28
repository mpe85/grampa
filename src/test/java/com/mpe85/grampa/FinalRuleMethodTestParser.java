package com.mpe85.grampa;

import com.mpe85.grampa.rule.Rule;

public class FinalRuleMethodTestParser extends TestParser {
	
	@Override
	protected final Rule<String> noop() {
		return empty();
	}
	
	
}

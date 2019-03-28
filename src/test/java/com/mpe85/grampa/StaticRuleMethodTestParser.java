package com.mpe85.grampa;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.EmptyRule;

public class StaticRuleMethodTestParser extends TestParser {
	
	protected static Rule<String> staticRule() {
		return new EmptyRule<>();
	}
	
	
}

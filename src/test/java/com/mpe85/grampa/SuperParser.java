package com.mpe85.grampa;

import com.mpe85.grampa.rule.Rule;

public class SuperParser extends TestParser {
	
	@Override
	public Rule<String> root() {
		return sequence(character('a'), this.root());
	}
	
}

package com.mpe85.grampa;

import com.mpe85.grampa.rule.Rule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class SuperParser extends TestParser {
	
	@Override
	@SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
	public Rule<String> root() {
		return sequence(character('a'), this.root());
	}
	
}

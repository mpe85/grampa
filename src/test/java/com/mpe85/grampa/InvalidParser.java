package com.mpe85.grampa;

import com.mpe85.grampa.parser.AbstractParser;
import com.mpe85.grampa.rule.Rule;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class InvalidParser extends AbstractParser<String> {
	
	public InvalidParser() {
	}
	
	@Override
	public Rule<String> root() {
		return expr('a');
	}
	
	@SuppressFBWarnings("IL_INFINITE_RECURSIVE_LOOP")
	private Rule<String> expr(final char c) {
		return firstOf(
				character(c),
				sequence(
						character('('),
						expr(c),
						character(')')));
	}
	
}

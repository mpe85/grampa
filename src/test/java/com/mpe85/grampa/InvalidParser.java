package com.mpe85.grampa;

import com.mpe85.grampa.parser.AbstractParser;
import com.mpe85.grampa.rule.Rule;

public class InvalidParser extends AbstractParser<String> {
	
	public InvalidParser() {
	}
	
	@Override
	public Rule<String> root() {
		return expr('a');
	}
	
	private Rule<String> expr(final char c) {
		return firstOf(
				character(c),
				sequence(
						character('('),
						expr(c),
						character(')')));
	}
	
}

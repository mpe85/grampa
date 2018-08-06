package com.mpe85.grampa;

import com.mpe85.grampa.parser.AbstractParser;
import com.mpe85.grampa.rule.Rule;

public class TestParser extends AbstractParser<String> {
	
	public TestParser() {
	}
	
	public TestParser(final String dummy) {
		this.dummy = dummy;
	}
	
	public String getDummy() {
		return dummy;
	}
	
	@Override
	public Rule<String> root() {
		return expr('a');
	}
	
	protected Rule<String> expr(final char c) {
		return firstOf(
				character(c),
				sequence(
						empty(),
						root(66),
						character('('),
						root(),
						character(')')));
		
	}
	
	protected Rule<String> root(final int i) {
		return empty();
	}
	
	private String dummy;
}

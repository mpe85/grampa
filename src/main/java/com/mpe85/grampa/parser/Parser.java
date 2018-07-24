package com.mpe85.grampa.parser;

import com.mpe85.grampa.rule.Rule;


public interface Parser<T> {
	
	Rule<T> root();
	
}

package com.mpe85.grampa.parser;

import com.mpe85.grampa.matcher.Rule;


public interface Parser<T> {
	
	Rule<T> root();
	
}

package com.mpe85.grampa.parser;

import com.mpe85.grampa.matcher.IMatcher;


public interface IParser<T> {
	
	IMatcher<T> root();
	
}

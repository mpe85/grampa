package com.mpe85.grampa.visitor;

import com.mpe85.grampa.rule.impl.AbstractRule;

public interface RuleVisitor<T> {
	
	void visit(AbstractRule<T> rule);
	
}

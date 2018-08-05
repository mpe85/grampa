package com.mpe85.grampa.rule;

import java.util.List;

import com.mpe85.grampa.visitor.RuleVisitor;

public interface Rule<T> {
	
	List<Rule<T>> getChildren();
	
	Rule<T> getChild();
	
	Rule<T> replaceReferenceRule(int index, Rule<T> replacementRule);
	
	boolean match(RuleContext<T> context);
	
	boolean isPredicate();
	
	void accept(RuleVisitor<T> visitor);
	
}

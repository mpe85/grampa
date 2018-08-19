package com.mpe85.grampa.visitor;

import com.mpe85.grampa.rule.Rule;
import com.mpe85.grampa.rule.impl.AbstractRule;

/**
 * Visitor for {@link Rule}s.
 * 
 * @author mpe85
 *
 * @param <T>
 *        type of the parser stack values
 */
public interface RuleVisitor<T> {
	
	/**
	 * Visits an abstract rule.
	 * 
	 * @param rule
	 *        the rule to visit
	 */
	void visit(AbstractRule<T> rule);
	
}

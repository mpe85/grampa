package com.mpe85.grampa.rule;

import java.util.List;

import com.mpe85.grampa.visitor.RuleVisitor;

/**
 * Defines a parser rule.
 * 
 * @author mpe85
 *
 * @param <T>
 *            the type of the stack elements
 */
public interface Rule<T> {
	
	/**
	 * Gets the child rules of the rule.
	 * 
	 * @return a list of parser rules
	 */
	List<Rule<T>> getChildren();
	
	/**
	 * Gets the (first) child rule of the rule.
	 * 
	 * @return a parser rule
	 */
	Rule<T> getChild();
	
	/**
	 * Replaces a reference rule inside the list of child rules.
	 * 
	 * @param index
	 *            the position if the reference rule inside the list of child rules
	 * @param replacementRule
	 *            a replacement rule
	 * @return the replaced reference rule
	 */
	Rule<T> replaceReferenceRule(int index, Rule<T> replacementRule);
	
	/**
	 * Try to match the rule
	 * 
	 * @param context
	 *            a rule context.
	 * @return true if the rule matched successfully, otherwise false
	 */
	boolean match(RuleContext<T> context);
	
	/**
	 * Checks if the rule is part of a predicate rule.
	 * 
	 * @return true if the rule is a direct or indirect child of a predicate rule, otherwise false
	 */
	boolean isPredicate();
	
	/**
	 * Accepts a rule visitor.
	 * 
	 * @param visitor
	 *            a rule visitor
	 */
	void accept(RuleVisitor<T> visitor);
	
}

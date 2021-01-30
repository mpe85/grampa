package com.mpe85.grampa.visitor

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.AbstractRule

/**
 * Visitor for [Rule]s.
 *
 * @author mpe85
 *
 * @param[T] The type of the parser stack values
 */
public interface RuleVisitor<T> {

    /**
     * Visit an abstract rule.
     *
     * @param[rule] The rule to visit
     */
    public fun visit(rule: AbstractRule<T>)
}

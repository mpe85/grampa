package com.github.mpe85.grampa.visitor

import com.github.mpe85.grampa.rule.AbstractRule
import com.github.mpe85.grampa.rule.Rule

/**
 * Visitor for [Rule]s.
 *
 * @param[T] The type of the parser stack values
 * @author mpe85
 */
public interface RuleVisitor<T> {

    /**
     * Visit an abstract rule.
     *
     * @param[rule] The rule to visit
     */
    public fun visit(rule: AbstractRule<T>)
}

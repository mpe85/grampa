package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.visitor.RuleVisitor

/**
 * The grammar rule interface.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[children] The child rules of the rule
 * @property[child] The (first) child rule of the rule
 * @property[testRule] Indicates if the rule is directly or indirectly part of a test rule.
 */
public interface Rule<T> {

    public val children: List<Rule<T>>
    public val child: Rule<T>?
    public val testRule: Boolean

    /**
     * Replace a reference rule inside the list of child rules.
     *
     * @param[index] The position if the reference rule inside the list of child rules
     * @param[replacementRule] A replacement rule
     * @return The replaced reference rule
     */
    public fun replaceReferenceRule(index: Int, replacementRule: Rule<T>): Rule<T>

    /**
     * Try to match the rule against the input.
     *
     * @param[context] A rule context
     * @return true if the rule matched successfully
     */
    public fun match(context: ParserContext<T>): Boolean

    /**
     * Accept a [RuleVisitor].
     *
     * @param visitor A rule visitor
     */
    public fun accept(visitor: RuleVisitor<T>)
}

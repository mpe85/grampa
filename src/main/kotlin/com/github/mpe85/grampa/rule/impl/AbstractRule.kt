package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.rule.ReferenceRule
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.visitor.RuleVisitor
import java.util.Objects.hash

/**
 * An abstract rule that is base for all rule implementations.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[children] A list of child rules
 */
public abstract class AbstractRule<T>(children: List<Rule<T>> = emptyList()) : Rule<T> {

    private val internalChildren = children.toMutableList()

    /**
     * Construct an abstract rule with one child rule.
     *
     * @param[child] A child rule
     */
    protected constructor(child: Rule<T>) : this(listOf(child))

    override val children: List<Rule<T>> get() = internalChildren

    override val child: Rule<T>? get() = internalChildren.getOrNull(0)

    override val testRule: Boolean = false

    override fun replaceReferenceRule(index: Int, replacementRule: Rule<T>): Rule<T> {
        require(index in children.indices) { "An 'index' must not be out of bounds." }
        require(children[index] is ReferenceRule<*>) { "Only reference rules can be replaced." }
        return internalChildren.set(index, replacementRule)
    }

    override fun accept(visitor: RuleVisitor<T>): Unit = visitor.visit(this)
    override fun hashCode(): Int = hash(children, testRule)
    override fun equals(other: Any?): Boolean =
        checkEquality(other, properties = arrayOf({ it.children }, { it.testRule }))
}

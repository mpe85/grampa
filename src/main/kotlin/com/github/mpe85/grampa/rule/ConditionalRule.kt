package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that runs one of two sub-rules, depending on a condition.
 *
 * @param[T] The type of the stack elements
 * @param[condition] A condition that is evaluated when the rule is run
 * @param[thenRule] A rule to run if the condition evaluates to true
 * @param[elseRule] An optional rule to run if the condition evaluates to false
 * @author mpe85
 */
internal class ConditionalRule<T>(
    private val condition: (RuleContext<T>) -> Boolean,
    thenRule: Rule<T>,
    elseRule: Rule<T>? = null,
) : AbstractRule<T>(elseRule?.let { listOf(thenRule, it) } ?: listOf(thenRule)) {

    private val thenRule
        get() = checkNotNull(child)

    private val elseRule
        get() = children.getOrNull(1)

    override fun match(context: ParserContext<T>): Boolean =
        if (condition(context)) thenRule.match(context) else elseRule?.match(context) ?: true

    override fun hashCode(): Int = hash(super.hashCode(), condition, thenRule, elseRule)

    override fun equals(other: Any?): Boolean =
        checkEquality(
            other,
            { super.equals(other) },
            { it.condition },
            { it.thenRule },
            { it.elseRule },
        )

    override fun toString(): String =
        stringify(
            "condition" to condition,
            "thenRule" to thenRule::class.simpleName,
            "elseRule" to elseRule?.let { it::class.simpleName },
        )
}

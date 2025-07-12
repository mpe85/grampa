package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.context.RuleContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * An action rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[action] The action that is executed by the rule
 * @param[skippable] Whether the action is skippable inside test rules
 * @author mpe85
 */
internal class ActionRule<T>(
    private val action: (RuleContext<T>) -> Boolean,
    private val skippable: Boolean = false,
) : AbstractRule<T>() {

    override fun match(context: ParserContext<T>): Boolean =
        context.inTestRule && skippable || action(context)

    override fun hashCode(): Int = hash(super.hashCode(), action, skippable)

    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.action }, { it.skippable })

    override fun toString(): String = stringify("action" to action, "skippable" to skippable)
}

@file:JvmName("ChoiceRules")

package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.rule.AbstractRule
import com.github.mpe85.grampa.rule.Rule
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that matches the first successful rule of its child rules.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 */
internal class ChoiceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

    override fun match(context: ParserContext<T>): Boolean = children.any { context.createChildContext(it).run() }

    override fun hashCode(): Int = hash(super.hashCode())
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })
    override fun toString(): String = stringify("#children" to children.size)
}

/**
 * Create a [ChoiceRule] out of this and another rule.
 *
 * @param[other] Another rule
 * @return A [ChoiceRule] with [this] and [other] as its child rules
 */
public infix fun <T> Rule<T>.or(other: Rule<T>): Rule<T> = ChoiceRule(listOf(this, other))

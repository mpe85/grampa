package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A sequence rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[rules] A list of child rules
 * @author mpe85
 */
internal class SequenceRule<T>(rules: List<Rule<T>>) : AbstractRule<T>(rules) {

    override fun match(context: ParserContext<T>): Boolean =
        children.all { context.createChildContext(it).run() }

    override fun hashCode(): Int = hash(super.hashCode())

    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })

    override fun toString(): String = stringify("#children" to children.size)
}

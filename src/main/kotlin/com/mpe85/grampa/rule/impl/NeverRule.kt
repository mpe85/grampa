package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that never matches.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class NeverRule<T> : AbstractRule<T>() {

    override fun match(context: ParserContext<T>) = false

    override fun hashCode() = hash(super.hashCode())
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) })
    override fun toString() = stringify()
}

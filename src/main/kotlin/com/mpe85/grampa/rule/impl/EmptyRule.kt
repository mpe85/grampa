package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that matches an empty input.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class EmptyRule<T> : AbstractRule<T>() {

    override fun match(context: ParserContext<T>) = true

    override fun hashCode() = hash(super.hashCode())
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) })
    override fun toString() = stringify()

}

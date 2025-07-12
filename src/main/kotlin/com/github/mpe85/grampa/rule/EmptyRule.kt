package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that matches an empty input.
 *
 * @param[T] The type of the stack elements
 * @author mpe85
 */
internal class EmptyRule<T> : AbstractRule<T>() {

    override fun match(context: ParserContext<T>): Boolean = true

    override fun hashCode(): Int = hash(super.hashCode())

    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })

    override fun toString(): String = stringify()
}

package com.github.mpe85.grampa.rule.impl

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.rule.AbstractRule
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A rule implementation that matches the end of the input.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
internal class EndOfInputRule<T> : AbstractRule<T>() {

    override fun match(context: ParserContext<T>): Boolean = context.atEndOfInput

    override fun hashCode(): Int = hash(super.hashCode())
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) })
    override fun toString(): String = stringify()
}

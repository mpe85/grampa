package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A character predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) : AbstractRule<T>() {

    /**
     * Construct a character predicate rule that exactly matches a specific character.
     *
     * @param[character] A character
     */
    constructor(character: Char) : this({ c -> c == character })

    override fun match(context: ParserContext<T>) = !context.atEndOfInput
            && predicate(context.currentChar)
            && context.advanceIndex(1)

    override fun hashCode() = hash(super.hashCode(), predicate)
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.predicate })
    override fun toString() = stringify("predicate" to predicate)

}

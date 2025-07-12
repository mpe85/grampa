package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.lang.Character.toUpperCase
import java.util.Objects.hash

/**
 * A character predicate rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[predicate] A predicate that is tested by the rule
 * @author mpe85
 */
internal open class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) :
    AbstractRule<T>() {

    /**
     * Construct a character predicate rule that exactly matches a specific character.
     *
     * @param[character] A character
     */
    constructor(character: Char) : this({ it == character })

    override fun match(context: ParserContext<T>): Boolean =
        !context.atEndOfInput && predicate(context.currentChar) && context.advanceIndex(1)

    override fun hashCode(): Int = hash(super.hashCode(), predicate)

    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.predicate })

    override fun toString(): String = stringify("predicate" to predicate)
}

/**
 * An ignore-case character rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[character] A character
 * @author mpe85
 */
internal class IgnoreCaseCharRule<T>(character: Char) :
    CharPredicateRule<T>({ toUpperCase(it) == toUpperCase(character) })

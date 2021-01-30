package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
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
public class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) : AbstractRule<T>() {

    /**
     * Construct a character predicate rule that exactly matches a specific character.
     *
     * @param[character] A character
     */
    public constructor(character: Char) : this({ c -> c == character })

    override fun match(context: ParserContext<T>): Boolean = !context.atEndOfInput
            && predicate(context.currentChar)
            && context.advanceIndex(1)

    override fun hashCode(): Int = hash(super.hashCode(), predicate)
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) }, { it.predicate })
    override fun toString(): String = stringify("predicate" to predicate)
}

/**
 * Create a rule from this character.
 *
 * @return A [CharPredicateRule]
 */
public fun <T> Char.toRule(): Rule<T> = CharPredicateRule(this)

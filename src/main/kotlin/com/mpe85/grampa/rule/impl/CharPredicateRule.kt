package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.lang.Character.toUpperCase
import java.util.Objects.hash

/**
 * A character predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
public open class CharPredicateRule<T>(private val predicate: (Char) -> Boolean) : AbstractRule<T>() {

    /**
     * Construct a character predicate rule that exactly matches a specific character.
     *
     * @param[character] A character
     */
    public constructor(character: Char) : this({ it == character })

    override fun match(context: ParserContext<T>): Boolean = !context.atEndOfInput
            && predicate(context.currentChar)
            && context.advanceIndex(1)

    override fun hashCode(): Int = hash(super.hashCode(), predicate)
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) }, { it.predicate })
    override fun toString(): String = stringify("predicate" to predicate)
}

/**
 * An ignore-case character rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[character] A character
 */
public class IgnoreCaseCharRule<T>(character: Char) :
    CharPredicateRule<T>({ toUpperCase(it) == toUpperCase(character) })

/**
 * Create a rule from this character.
 *
 * @return A [CharPredicateRule]
 */
public fun <T> Char.toRule(): Rule<T> = CharPredicateRule(this)

/**
 * Create an ignore-case rule from this character.
 *
 * @return A [CharPredicateRule]
 */
public fun <T> Char.toIgnoreCaseRule(): Rule<T> = IgnoreCaseCharRule(this)

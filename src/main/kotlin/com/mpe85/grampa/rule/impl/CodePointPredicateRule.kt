package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.charCount
import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A code point predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
class CodePointPredicateRule<T>(private val predicate: (Int) -> Boolean) : AbstractRule<T>() {

    /**
     * Construct a code point predicate rule that exactly matches a specific code point.
     *
     * @param[codePoint] A code point
     */
    constructor(codePoint: Int) : this({ cp -> cp == codePoint })

    /**
     * Construct a code point predicate rule that exactly matches a specific character.
     *
     * @param[char] A character
     */
    constructor(char: Char) : this({ cp -> cp == char.toInt() })

    override fun match(context: ParserContext<T>) = !context.atEndOfInput &&
            predicate(context.currentCodePoint) &&
            context.advanceIndex(charCount(context.currentCodePoint))

    override fun hashCode() = hash(super.hashCode(), predicate)
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.predicate })
    override fun toString() = stringify("predicate" to predicate)
}

/**
 * Create a rule from this code point.
 *
 * @return A [CodePointPredicateRule]
 */
fun <T> Int.toRule() = CodePointPredicateRule<T>(this)

package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import com.ibm.icu.lang.UCharacter.charCount
import com.ibm.icu.lang.UCharacter.foldCase
import com.ibm.icu.lang.UCharacter.toString
import java.util.Objects.hash

/**
 * A code point predicate rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[predicate] A predicate that is tested by the rule
 */
internal open class CodePointPredicateRule<T>(private val predicate: (Int) -> Boolean) : AbstractRule<T>() {

    /**
     * Construct a code point predicate rule that exactly matches a specific code point.
     *
     * @param[codePoint] A code point
     */
    constructor(codePoint: Int) : this({ it == codePoint })

    override fun match(context: ParserContext<T>): Boolean = !context.atEndOfInput &&
        predicate(context.currentCodePoint) &&
        context.advanceIndex(charCount(context.currentCodePoint))

    override fun hashCode(): Int = hash(super.hashCode(), predicate)
    override fun equals(other: Any?): Boolean = checkEquality(other, { super.equals(other) }, { it.predicate })
    override fun toString(): String = stringify("predicate" to predicate)
}

/**
 * An ignore-case code point rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[codePoint] A code point
 */
internal class IgnoreCaseCodePointRule<T>(codePoint: Int) :
    CodePointPredicateRule<T>({ foldCase(toString(it), true) == foldCase(toString(codePoint), true) })

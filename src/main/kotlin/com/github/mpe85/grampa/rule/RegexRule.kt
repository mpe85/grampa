package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import java.util.Objects.hash
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

/**
 * A regular expression rule implementation.
 *
 * @param[T] The type of the stack elements
 * @param[pattern] A compiled regular expression
 * @author mpe85
 */
internal class RegexRule<T>(private val pattern: Pattern) : AbstractRule<T>() {

    /**
     * Construct a regex rule using a regex string.
     *
     * @param[regex] A string containing a regular expression
     */
    constructor(regex: String) : this(compile(regex))

    override fun match(context: ParserContext<T>): Boolean =
        pattern.matcher(context.restOfInput).let { matcher ->
            matcher.lookingAt() && context.advanceIndex(matcher.end())
        }

    override fun hashCode(): Int = hash(super.hashCode(), pattern)

    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.pattern })

    override fun toString(): String = stringify("pattern" to pattern)
}

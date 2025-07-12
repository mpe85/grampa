package com.github.mpe85.grampa.rule

import com.github.mpe85.grampa.context.ParserContext
import com.github.mpe85.grampa.util.checkEquality
import com.github.mpe85.grampa.util.stringify
import com.ibm.icu.lang.UCharacter
import com.ibm.icu.lang.UCharacter.charCount
import com.ibm.icu.lang.UCharacter.toString
import com.ibm.icu.util.BytesTrie.Result.FINAL_VALUE
import com.ibm.icu.util.BytesTrie.Result.INTERMEDIATE_VALUE
import com.ibm.icu.util.BytesTrie.Result.NO_MATCH
import com.ibm.icu.util.CharsTrieBuilder
import com.ibm.icu.util.StringTrieBuilder.Option.FAST
import java.util.Objects.hash
import kotlin.streams.asSequence
import kotlin.streams.toList

/**
 * A case-sensitive trie (prefix tree) rule implementation that matches the input against a
 * dictionary.
 *
 * @param[T] The type of the stack elements
 * @param[strings] A collection of strings from which the trie is built up
 * @author mpe85
 */
internal open class TrieRule<T>(private val strings: Collection<String>) : AbstractRule<T>() {

    private val trie =
        CharsTrieBuilder().run {
            strings.asSequence().map(::map).distinct().forEach { add(it, 0) }
            build(FAST)
        }

    protected open fun map(string: String): String = string

    /**
     * Construct a case-sensitive trie.
     *
     * @param[strings] A variable number of strings
     */
    constructor(vararg strings: String) : this(strings.toList())

    override fun match(context: ParserContext<T>): Boolean =
        try {
            var longestMatch = 0
            for ((idx, codePoint) in context.restOfInput.codePoints().asSequence().withIndex()) {
                val foldedCodePoints = map(toString(codePoint)).codePoints().toList()
                foldedCodePoints.dropLast(1).forEach { trie.nextForCodePoint(it) }
                val result = trie.nextForCodePoint(foldedCodePoints.last())
                if (result in setOf(FINAL_VALUE, INTERMEDIATE_VALUE)) {
                    longestMatch = idx + 1
                }
                if (result in setOf(FINAL_VALUE, NO_MATCH)) {
                    break
                }
            }
            val charCount =
                context.restOfInput.codePoints().asSequence().take(longestMatch).sumOf {
                    charCount(it)
                }
            longestMatch > 0 && context.advanceIndex(charCount)
        } finally {
            trie.reset()
        }

    override fun hashCode(): Int = hash(super.hashCode(), strings)

    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.strings })

    override fun toString(): String = stringify("strings" to strings)
}

/**
 * A case-insensitive trie (prefix tree) rule implementation that matches the input against a
 * dictionary.
 *
 * @param[T] The type of the stack elements
 * @param[strings] A collection of strings from which the trie is built up
 * @author mpe85
 */
internal class IgnoreCaseTrieRule<T>(strings: Collection<String>) : TrieRule<T>(strings) {

    /**
     * Construct a case-insensitive trie.
     *
     * @param[strings] A variable number of strings
     */
    constructor(vararg strings: String) : this(strings.toList())

    override fun map(string: String): String = UCharacter.foldCase(string, true)
}

package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.charCount
import com.ibm.icu.lang.UCharacter.foldCase
import com.ibm.icu.lang.UCharacter.toString
import com.ibm.icu.util.BytesTrie.Result.FINAL_VALUE
import com.ibm.icu.util.BytesTrie.Result.INTERMEDIATE_VALUE
import com.ibm.icu.util.BytesTrie.Result.NO_MATCH
import com.ibm.icu.util.CharsTrieBuilder
import com.ibm.icu.util.StringTrieBuilder.Option.FAST
import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash
import kotlin.streams.asSequence
import kotlin.streams.toList

/**
 * A trie (prefix tree) rule implementation that matches the input against a dictionary.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[strings] A collection of strings from which the trie is built up
 * @property[ignoreCase] Indicates if the case of the strings should be ignored
 */
public class TrieRule<T> @JvmOverloads constructor(
    private val strings: Collection<String>,
    private val ignoreCase: Boolean = false
) : AbstractRule<T>() {

    private val trie = CharsTrieBuilder().run {
        strings.forEach { add(if (ignoreCase) foldCase(it, true) else it, 0) }
        build(FAST)
    }

    /**
     * Construct a case-sensitive trie.
     *
     * @param[strings] A variable number of strings
     */
    public constructor(vararg strings: String) : this(strings.toList())

    /**
     * Construct a case-sensitive or case-insensitive trie.
     *
     * @param[ignoreCase] Indicates if the case of the strings should be ignored
     * @param[strings] A variable number of strings
     */
    public constructor(ignoreCase: Boolean, vararg strings: String) : this(strings.toSet(), ignoreCase)

    override fun match(context: ParserContext<T>): Boolean = try {
        var longestMatch = 0
        loop@ for ((idx, codePoint) in context.restOfInput.codePoints().asSequence().withIndex()) {
            val foldedCodePoints =
                if (ignoreCase) foldCase(toString(codePoint), true).codePoints().toList() else listOf(codePoint)
            for (foldedCodePoint in foldedCodePoints.dropLast(1)) {
                if (trie.nextForCodePoint(foldedCodePoint) in setOf(FINAL_VALUE, NO_MATCH)) {
                    break@loop
                }
            }
            val result = trie.nextForCodePoint(foldedCodePoints.last())
            if (result in setOf(FINAL_VALUE, INTERMEDIATE_VALUE)) {
                longestMatch = idx + 1
            }
            if (result in setOf(FINAL_VALUE, NO_MATCH)) {
                break@loop
            }
        }
        val charCount = context.restOfInput.codePoints().asSequence().take(longestMatch).sumBy { charCount(it) }
        longestMatch > 0 && context.advanceIndex(charCount)
    } finally {
        trie.reset()
    }

    override fun hashCode(): Int = hash(super.hashCode(), strings, ignoreCase)
    override fun equals(other: Any?): Boolean =
        checkEquality(other, { super.equals(other) }, { it.strings }, { it.ignoreCase })

    override fun toString(): String = stringify("strings" to strings, "ignoreCase" to ignoreCase)
}

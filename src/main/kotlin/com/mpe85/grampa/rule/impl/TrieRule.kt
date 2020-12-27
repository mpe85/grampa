package com.mpe85.grampa.rule.impl

import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.util.BytesTrie.Result.FINAL_VALUE
import com.ibm.icu.util.BytesTrie.Result.INTERMEDIATE_VALUE
import com.ibm.icu.util.BytesTrie.Result.NO_MATCH
import com.ibm.icu.util.CharsTrieBuilder
import com.ibm.icu.util.StringTrieBuilder.Option.FAST
import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash

/**
 * A trie (prefix tree) rule implementation that matches the input against a dictionary.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[strings] A collection of strings from which the trie is built up
 * @property[ignoreCase] Indicates if the case of the strings should be ignored
 * @property[strings] A set containing all strings inside the trie
 */
class TrieRule<T> @JvmOverloads constructor(strings: Collection<String>, private val ignoreCase: Boolean = false) :
  AbstractRule<T>() {

  private val trie = CharsTrieBuilder().run {
    strings.forEach { s ->
      add(if (ignoreCase) toLowerCase(s) else s, 0)
    }
    build(FAST)
  }

  /**
   * Construct a case-sensitive trie.
   *
   * @param[strings] A variable number of strings
   */
  constructor(vararg strings: String) : this(strings.toList())

  /**
   * Construct a case-sensitive or case-insensitive trie.
   *
   * @param[ignoreCase] Indicates if the case of the strings should be ignored
   * @param[strings] A variable number of strings
   */
  constructor(ignoreCase: Boolean, vararg strings: String) : this(strings.toSet(), ignoreCase)

  private val strings: Set<String>
    get() = trie.iterator().asSequence()
      .map { entry -> entry.chars.toString() }
      .toSet()

  override fun match(context: ParserContext<T>): Boolean {
    var longestMatch = 0
    val codePoints = context.restOfInput.codePoints().toArray()
    for (i in codePoints.indices) {
      val result = trie.next(if (ignoreCase) toLowerCase(codePoints[i]) else codePoints[i])
      if (result in setOf(FINAL_VALUE, INTERMEDIATE_VALUE)) {
        longestMatch = i + 1
      }
      if (result in setOf(FINAL_VALUE, NO_MATCH)) {
        break
      }
    }
    trie.reset()
    return longestMatch > 0 && context.advanceIndex(longestMatch)
  }

  override fun hashCode() = hash(super.hashCode(), strings, ignoreCase)
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.strings }, { it.ignoreCase })
  override fun toString() = stringify("strings" to strings, "ignoreCase" to ignoreCase)

}

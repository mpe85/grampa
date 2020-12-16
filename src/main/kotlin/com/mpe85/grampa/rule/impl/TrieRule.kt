package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.ibm.icu.lang.UCharacter.toLowerCase
import com.ibm.icu.util.BytesTrie.Result.FINAL_VALUE
import com.ibm.icu.util.BytesTrie.Result.INTERMEDIATE_VALUE
import com.ibm.icu.util.BytesTrie.Result.NO_MATCH
import com.ibm.icu.util.CharsTrie
import com.ibm.icu.util.CharsTrieBuilder
import com.ibm.icu.util.StringTrieBuilder.Option.FAST
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A trie (prefix tree) rule implementation that matches the input against a dictionary.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param strings a set of strings
 * @property ignoreCase if true, the case of the strings is ignored
 */
class TrieRule<T> @JvmOverloads constructor(strings: Set<String>, private val ignoreCase: Boolean = false) :
  AbstractRule<T>() {

  private val trie: CharsTrie = CharsTrieBuilder().apply {
    strings.forEach { s ->
      add(if (ignoreCase) toLowerCase(s) else s, 0)
    }
  }.build(FAST)

  /**
   * C'tor. Construct a case-sensitive trie.
   *
   * @param strings a variable number of strings
   */
  constructor(vararg strings: String) : this(strings.toSet())

  /**
   * C'tor. Construct a trie (case-sensitive or case-insensitive).
   *
   * @param ignoreCase if true, the case of the strings is ignored
   * @param strings a variable number of strings
   */
  constructor(ignoreCase: Boolean, vararg strings: String) : this(strings.toSet(), ignoreCase)

  /**
   * Returns all strings inside the trie.
   *
   * @return a set of strings
   */
  private val strings: Set<String>
    get() = trie.iterator().asSequence()
      .map { entry -> entry.chars.toString() }
      .toSet()

  override fun match(context: RuleContext<T>): Boolean {
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

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as TrieRule<*>
      return super.equals(other)
          && strings == other.strings
          && ignoreCase == other.ignoreCase
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("strings", strings)
    .add("ignoreCase", ignoreCase)

}

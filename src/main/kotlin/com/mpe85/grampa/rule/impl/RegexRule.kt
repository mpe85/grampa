package com.mpe85.grampa.rule.impl

import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import java.util.Objects.hash
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

/**
 * A regular expression rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[pattern] A compiled regular expression
 */
class RegexRule<T>(private val pattern: Pattern) : AbstractRule<T>() {

  /**
   * Construct a regex rule using a regex string.
   *
   * @param[regex] A string containing a regular expression
   */
  constructor(regex: String) : this(compile(regex))

  override fun match(context: RuleContext<T>) = pattern.matcher(context.restOfInput).let { matcher ->
    matcher.lookingAt() && context.advanceIndex(matcher.end())
  }

  override fun hashCode() = hash(super.hashCode(), pattern)
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, RegexRule<T>::pattern)
  override fun toString() = stringify(RegexRule<T>::pattern)

}

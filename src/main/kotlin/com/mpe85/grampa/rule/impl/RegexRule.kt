package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

/**
 * A regular expression rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param pattern a compiled regular expression
 */
class RegexRule<T>(private val pattern: Pattern) : AbstractRule<T>() {

  /**
   * C'tor. Constructs a regex rule using a regex string.
   *
   * @param regex a string containing a regular expression
   */
  constructor(regex: String) : this(compile(regex))

  override fun match(context: RuleContext<T>) = pattern.matcher(context.restOfInput).let { matcher ->
    matcher.lookingAt() && context.advanceIndex(matcher.end())
  }

  override fun hashCode() = hash(super.hashCode(), pattern)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as RegexRule<*>
      return super.equals(other)
          && pattern == other.pattern
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("pattern", pattern)

}

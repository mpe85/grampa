package com.mpe85.grampa.rule.impl

import au.com.console.kassava.kotlinEquals
import au.com.console.kassava.kotlinToString
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A repeat rule implementation.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[rule] The rule to repeat
 * @property[min] The minimum number of cycles
 * @property[max] An optional maximum number of cycles
 */
class RepeatRule<T>(private val rule: Rule<T>, private val min: Int, private val max: Int?) : AbstractRule<T>(rule) {

  init {
    require(min >= 0) { "A 'min' number must not be negative" }
    if (max != null) {
      require(max >= min) { "A 'max' number must not be lower than the 'min' number." }
    }
  }

  override fun match(context: RuleContext<T>): Boolean {
    var iterations = 0
    while (max == null || iterations < max) {
      if (!context.createChildContext(rule).run()) {
        break
      }
      iterations++
    }
    return iterations >= min
  }

  override fun hashCode() = hash(super.hashCode(), min, max)
  override fun equals(other: Any?) = kotlinEquals(other, properties)
  override fun toString() = kotlinToString(properties)

  companion object {
    private val properties = arrayOf(RepeatRule<*>::rule, RepeatRule<*>::min, RepeatRule<*>::max)
  }

}

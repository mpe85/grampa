package com.mpe85.grampa.rule.impl

import com.google.common.base.MoreObjects.ToStringHelper
import com.google.common.base.Preconditions.checkArgument
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import java.util.Objects.hash

/**
 * A repeat rule implementation.
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param rule the rule to repeat
 * @param min the minimum number of cycles
 * @param max an optional maximum number of cycles
 */
class RepeatRule<T>(rule: Rule<T>, private val min: Int, private val max: Int?) : AbstractRule<T>(rule) {

  init {
    checkArgument(min >= 0, "A 'min' number must not be negative")
    if (max != null) {
      checkArgument(max >= min, "A 'max' number must not be lower than the 'min' number.")
    }
  }

  override fun match(context: RuleContext<T>): Boolean {
    var iterations = 0
    while (max == null || iterations < max) {
      if (!context.createChildContext(child).run()) {
        break
      }
      iterations++
    }
    return iterations >= min
  }

  override fun hashCode() = hash(super.hashCode(), min, max)

  override fun equals(obj: Any?): Boolean {
    if (obj != null && javaClass == obj.javaClass) {
      val other = obj as RepeatRule<*>
      return super.equals(other)
          && min == other.min
          && max == other.max
    }
    return false
  }

  override fun toStringHelper(): ToStringHelper = super.toStringHelper()
    .add("min", min)
    .add("max", max)

}

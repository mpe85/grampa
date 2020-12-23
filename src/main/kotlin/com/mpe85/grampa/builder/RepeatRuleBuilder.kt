package com.mpe85.grampa.builder

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.RepeatRule

/**
 * A builder for repeat rules. See [RepeatRule].
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @param[rule] The repeated rule
 */
class RepeatRuleBuilder<T>(private val rule: Rule<T>) {

  /**
   * Repeat the rule exactly [number] times.
   *
   * @param[number] The number specifying how many times the rule is repeated
   * @return A repeat rule
   */
  operator fun times(number: Int) = RepeatRule(rule, number, number)

  /**
   * Repeat the rule between [min] and [max] times.
   *
   * @param[min] The number specifying how many times the rule must be repeated at least
   * @param[max] The number specifying how many times the rule can be repeated at most
   * @return A repeat rule
   */
  fun times(min: Int, max: Int) = RepeatRule(rule, min, max)

  /**
   * Repeat the rule at least [min] times.
   *
   * @param[min] The number specifying how many times the rule must be repeated at least
   * @return A repeat rule
   */
  fun min(min: Int) = RepeatRule(rule, min, null)

  /**
   * Repeat the rule at most [max] times.
   *
   * @param[max] The number specifying how many times the rule can be repeated at most
   * @return A repeat rule
   */
  fun max(max: Int) = RepeatRule(rule, 0, max)

}

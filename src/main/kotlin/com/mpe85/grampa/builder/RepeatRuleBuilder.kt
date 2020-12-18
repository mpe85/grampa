package com.mpe85.grampa.builder

import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.RepeatRule

/**
 * A builder for repeat rules. See [RepeatRule].
 *
 * @author mpe85
 * @param T the type of the stack elements
 * @param rule the repeated rule
 */
class RepeatRuleBuilder<T>(private val rule: Rule<T>) {

  /**
   * Repeats the rule exactly [number] times.
   *
   * @param number the number specifying how many times the rule is repeated
   * @return a repeat rule
   */
  operator fun times(number: Int) = RepeatRule(rule, number, number)

  /**
   * Repeats the rule between [min] and [max] times.
   *
   * @param min the number specifying how many times the rule must be repeated at least
   * @param max the number specifying how many times the rule can be repeated at most
   * @return a repeat rule
   */
  fun times(min: Int, max: Int) = RepeatRule(rule, min, max)

  /**
   * Repeats the rule at least [min] times.
   *
   * @param min the number specifying how many times the rule must be repeated at least
   * @return a repeat rule
   */
  fun min(min: Int) = RepeatRule(rule, min, null)

  /**
   * Repeats the rule at most [max] times.
   *
   * @param max the number specifying how many times the rule can be repeated at most
   * @return a repeat rule
   */
  fun max(max: Int) = RepeatRule(rule, 0, max)

}

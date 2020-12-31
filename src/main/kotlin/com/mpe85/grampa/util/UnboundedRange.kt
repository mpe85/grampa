package com.mpe85.grampa.util

/**
 * A range which maximum value may be unbounded.
 *
 * @property[min] The lower bound
 * @property[max] The upper bound, may be null i.e. unbounded
 */
data class UnboundedRange(val min: Int = 0, val max: Int? = null)

/**
 * Create an [UnboundedRange] with a given lower bound and no upper bound.
 *
 * @param[n] The min value
 * @return An [UnboundedRange]
 */
fun min(n: Int) = UnboundedRange(min = n)

/**
 * Create an [UnboundedRange] with a given upper bound and a zero lower bound.
 *
 * @param[n] The max value
 * @return An [UnboundedRange]
 */
fun max(n: Int) = UnboundedRange(max = n)

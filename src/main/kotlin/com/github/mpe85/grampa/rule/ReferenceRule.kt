package com.github.mpe85.grampa.rule

/**
 * A rule that references another rule.
 *
 * @property[referencedRuleHash] The hash value of the referenced rule
 * @author mpe85
 */
public interface ReferenceRule<T> : Rule<T> {

    public val referencedRuleHash: Int
}

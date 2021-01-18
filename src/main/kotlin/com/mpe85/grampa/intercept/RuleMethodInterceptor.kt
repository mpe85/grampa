package com.mpe85.grampa.intercept

import com.mpe85.grampa.context.ParserContext
import com.mpe85.grampa.grammar.Grammar
import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.impl.AbstractRule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import com.mpe85.grampa.visitor.impl.ReferenceRuleReplaceVisitor
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.SuperCall
import java.lang.reflect.Method
import java.util.Objects.hash
import java.util.concurrent.Callable
import kotlin.reflect.jvm.javaMethod

/**
 * An interceptor for the rule methods of a grammar.
 * If a rule method gets called for the second time, the actual rule is replaced by a reference rule in the first place.
 * At the very ending of the grammar creating process (i.e. the first call of the root rule method)
 * all reference rules are replaced by the 'real' rules.
 * his is done to avoid endless recursions caused by circular rule dependencies.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
internal class RuleMethodInterceptor<T> {

    private val rules: MutableMap<Int, Rule<T>?> = HashMap()
    private val rootRuleMethod = requireNotNull(Grammar<T>::root.javaMethod)

    /**
     * Intercept rule methods.
     *
     * @param[method] A rule method
     * @param[superCall] The method body wrapped inside a callable
     * @param[args] The arguments with which the method was called
     * @return A grammar rule
     */
    fun intercept(
        @Origin method: Method,
        @SuperCall superCall: Callable<Rule<T>>,
        @AllArguments vararg args: Any?
    ): Rule<T> = hash(method.name, args.contentHashCode()).let { hash ->
        if (rules.containsKey(hash)) ReferenceRuleImpl(hash) else {
            rules[hash] = null
            superCall.call().also { rule ->
                rules[hash] = rule
                if (method.isRoot()) {
                    rule.accept(ReferenceRuleReplaceVisitor(rules))
                }
            }
        }
    }

    /**
     * Check if a rule method is the root rule method.
     *
     * @return true if it is the root rule method
     */
    private fun Method.isRoot() = rootRuleMethod.name == name && rootRuleMethod.parameterCount == parameterCount
}

/**
 * A reference rule implementation that stores a reference to a another rule.
 * Note that this is only used by [RuleMethodInterceptor].
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 * @property[referencedRuleHash] The hash code of the referenced rule
 */
private class ReferenceRuleImpl<T>(override val referencedRuleHash: Int) : ReferenceRule<T>, AbstractRule<T>() {

    override fun match(context: ParserContext<T>) = false

    override fun hashCode() = hash(super.hashCode(), referencedRuleHash)
    override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.referencedRuleHash })
    override fun toString() = stringify("referencedRuleHash" to referencedRuleHash)
}

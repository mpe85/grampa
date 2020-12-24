package com.mpe85.grampa.intercept

import com.mpe85.grampa.rule.ReferenceRule
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.rule.RuleContext
import com.mpe85.grampa.rule.impl.AbstractRule
import com.mpe85.grampa.util.checkEquality
import com.mpe85.grampa.util.stringify
import com.mpe85.grampa.visitor.impl.ReferenceRuleReplaceVisitor
import java.lang.reflect.Method
import java.util.HashMap
import java.util.Objects.hash
import java.util.concurrent.Callable
import net.bytebuddy.implementation.bind.annotation.AllArguments
import net.bytebuddy.implementation.bind.annotation.Origin
import net.bytebuddy.implementation.bind.annotation.RuntimeType
import net.bytebuddy.implementation.bind.annotation.SuperCall

/**
 * An interceptor for the rule methods of a parser. If a rule method gets called for the second time, the actual rule is
 * replaced by a reference rule in the first place. At the very ending of the parser creating process (i.e. the first
 * call of the root rule method) all reference rules are replaced by the 'real' rules. This is done to avoid endless
 * recursions caused by circular rule dependencies.
 *
 * @author mpe85
 * @param[T] The type of the stack elements
 */
class RuleMethodInterceptor<T> {

  companion object {
    private const val ROOT = "root"
  }

  private val rules: MutableMap<Int, Rule<T>?> = HashMap()

  /**
   * Intercept rule methods.
   *
   * @param[method] A rule method
   * @param[superCallable] The method body wrapped inside a callable
   * @param[args] The arguments with which the method was called
   * @return A parser rule
   * @throws[Exception] may be thrown when the actual method is called via the callable
   */
  @RuntimeType
  @Throws(Exception::class)
  fun intercept(
    @Origin method: Method,
    @SuperCall superCallable: Callable<Rule<T>>,
    @AllArguments vararg args: Any?
  ): Rule<T> {
    val hash = hash(method.name, args.contentHashCode())
    if (!rules.containsKey(hash)) {
      rules[hash] = null
      val rule = superCallable.call()
      rules[hash] = rule
      if (method.isRoot()) {
        rule.accept(ReferenceRuleReplaceVisitor(rules))
      }
      return rule
    }
    return ReferenceRuleImpl(hash)
  }

  /**
   * Check if a rule method is the root rule method.
   *
   * @return true if it is the root rule method
   */
  private fun Method.isRoot() = ROOT == name && parameterCount == 0

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

  override fun match(context: RuleContext<T>) = false

  override fun hashCode() = hash(super.hashCode(), referencedRuleHash)
  override fun equals(other: Any?) = checkEquality(other, { super.equals(other) }, { it.referencedRuleHash })
  override fun toString() = stringify(ReferenceRuleImpl<T>::referencedRuleHash)

}

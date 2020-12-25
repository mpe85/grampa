package com.mpe85.grampa

import com.mpe85.grampa.exception.ParserCreateException
import com.mpe85.grampa.intercept.RuleMethodInterceptor
import com.mpe85.grampa.parser.Parser
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.isFinal
import com.mpe85.grampa.util.isPublicOrProtected
import com.mpe85.grampa.util.isSafeVarArgsRuleFunction
import com.mpe85.grampa.util.isSafeVarArgsRuleMethod
import com.mpe85.grampa.util.isStatic
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation.withDefaultConfiguration
import net.bytebuddy.matcher.ElementMatchers.returns

/**
 * Create a new parser instance using the given parser [KClass].
 * The parser class must have a no-args constructor which will be called by this function.
 *
 * @param[U] The type of the parser
 * @param[T] The type of the stack elements
 * @return A parser instance
 */
fun <U : Parser<T>, T> KClass<U>.createParser() = try {
  createParserSubClass().createInstance()
} catch (ex: IllegalArgumentException) {
  throw ParserCreateException("Failed to create new parser instance.", ex)
}

/**
 * Create a new parser instance using the given parser [Class].
 * The parser class must have a no-args constructor which will be called by this function.
 *
 * @param[U] The type of the parser
 * @param[T] The type of the stack elements
 * @return A parser instance
 */
fun <U : Parser<T>, T> Class<U>.createParser() = kotlin.createParser()

/**
 * Create a new parser instance using the given parser [KClass] and constructor arguments.
 * The parser class must have a constructor which matches the passed argument types.
 *
 * @param[U] The type of the parser
 * @param[T] The type of the stack elements
 * @param[args] The constructor arguments
 * @return A parser instance
 */
fun <U : Parser<T>, T> KClass<U>.createParser(vararg args: Any?): U {
  for (constructor in createParserSubClass().constructors) {
    try {
      return constructor.call(*args)
    } catch (ex: IllegalArgumentException) {
      continue
    }
  }
  throw ParserCreateException("Failed to find a constructor that is callable with the given arguments.")
}

/**
 * Create a new parser instance using the given parser [Class] and constructor arguments.
 * The parser class must have a constructor which matches the passed argument types.
 *
 * @param[U] The type of the parser
 * @param[T] The type of the stack elements
 * @param[args] The constructor arguments
 * @return A parser instance
 */
fun <U : Parser<T>, T> Class<U>.createParser(vararg args: Any?) = kotlin.createParser(args)

private fun <U : Parser<T>, T> KClass<U>.createParserSubClass(): KClass<out U> {
  validate()
  return ByteBuddy()
    .subclass(java)
    .method(returns(Rule::class.java))
    .intercept(withDefaultConfiguration().to(RuleMethodInterceptor<Any>()))
    .make()
    .load(java.classLoader)
    .loaded
    .kotlin
}

private fun KClass<*>.validate() {
  declaredFunctions.asSequence().filter { it.returnType.jvmErasure.isSubclassOf(Rule::class) }.forEach {
    it.requireOverridable()
    it.javaMethod?.requireOverridable()
  }
  superclasses.forEach {
    it.validate()
  }
}

private fun KFunction<*>.requireOverridable() {
  if (!isPublicOrProtected() || isFinal && !isSafeVarArgsRuleFunction()) {
    throw ParserCreateException("The rule method '$this' must be overridable.")
  }
}

private fun Method.requireOverridable() {
  if (!isPublicOrProtected() || isFinal() && !isSafeVarArgsRuleMethod() || isStatic()) {
    throw ParserCreateException("The rule method '$this' must be overridable.")
  }
}

@file:JvmName("Grampa")

package com.mpe85.grampa

import com.mpe85.grampa.grammar.Grammar
import com.mpe85.grampa.intercept.RuleMethodInterceptor
import com.mpe85.grampa.rule.Rule
import com.mpe85.grampa.util.isFinal
import com.mpe85.grampa.util.isPublicOrProtected
import com.mpe85.grampa.util.isStatic
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation.withDefaultConfiguration
import net.bytebuddy.matcher.ElementMatchers.returns
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

/**
 * Create a new grammar instance using the given grammar [KClass].
 * The grammar class must have a no-args constructor which will be called by this function.
 *
 * @param[U] The type of the grammar
 * @param[T] The type of the stack elements
 * @return A grammar instance
 */
fun <U : Grammar<T>, T> KClass<U>.createGrammar() = createGrammarSubClass().createInstance()

/**
 * Create a new grammar instance using the given grammar [Class].
 * The grammar class must have a no-args constructor which will be called by this function.
 *
 * @param[U] The type of the grammar
 * @param[T] The type of the stack elements
 * @return A grammar instance
 */
fun <U : Grammar<T>, T> Class<U>.createGrammar() = kotlin.createGrammar()

/**
 * Create a new grammar instance using the given grammar [KClass] and constructor arguments.
 * The grammar class must have a constructor which matches the passed argument types.
 *
 * @param[U] The type of the grammar
 * @param[T] The type of the stack elements
 * @param[args] The constructor arguments
 * @return A grammar instance
 */
fun <U : Grammar<T>, T> KClass<U>.createGrammar(vararg args: Any?): U {
    for (constructor in createGrammarSubClass().constructors) {
        try {
            return constructor.call(*args)
        } catch (ex: IllegalArgumentException) {
            continue
        }
    }
    throw IllegalArgumentException("Failed to find a constructor that is callable with the given arguments.")
}

/**
 * Create a new grammar instance using the given parser [Class] and constructor arguments.
 * The grammar class must have a constructor which matches the passed argument types.
 *
 * @param[U] The type of the grammar
 * @param[T] The type of the stack elements
 * @param[args] The constructor arguments
 * @return A grammar instance
 */
fun <U : Grammar<T>, T> Class<U>.createGrammar(vararg args: Any?) = kotlin.createGrammar(args)

private fun <U : Grammar<T>, T> KClass<U>.createGrammarSubClass(): KClass<out U> {
    validate()
    return ByteBuddy()
        .subclass(java)
        .method(returns(Rule::class.java))
        .intercept(withDefaultConfiguration().to(RuleMethodInterceptor<T>()))
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
    superclasses.forEach { it.validate() }
}

private fun KFunction<*>.requireOverridable() = require(isPublicOrProtected() && !isFinal) {
    "The rule method '$this' must be overridable."
}

private fun Method.requireOverridable() = require(isPublicOrProtected() && !isFinal() && !isStatic()) {
    "The rule method '$this' must be overridable."
}
